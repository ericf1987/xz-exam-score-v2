package com.xz.examscore.asynccomponents.report;

import com.hyd.simplecache.utils.MD5;
import com.xz.ajiaedu.common.lang.Context;
import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.ajiaedu.common.xml.XmlNode;
import com.xz.ajiaedu.common.xml.XmlNodeReader;
import com.xz.examscore.AppException;
import com.xz.examscore.bean.Range;
import com.xz.examscore.services.ProvinceService;
import com.xz.examscore.services.RangeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.xz.ajiaedu.common.concurrent.Executors.newBlockingThreadPoolExecutor;

/**
 * (description)
 * created at 16/05/30
 *
 * @author yiding_he
 */
@Component
public class ReportManager implements ApplicationContextAware {

    static final Logger LOG = LoggerFactory.getLogger(ReportManager.class);

    private ThreadPoolExecutor executionPool;

    @Value("${report.generator.poolsize}")
    private int poolSize;

    @Value("${report.generator.savepath}")
    private String savePath;

    @Autowired
    ProvinceService provinceService;

    @Autowired
    RangeService rangeService;

    private XmlNode reportConfig;

    private ApplicationContext applicationContext;

    @PostConstruct
    public void init() {
        this.executionPool = newBlockingThreadPoolExecutor(poolSize, poolSize, 100);
        this.reportConfig = XmlNodeReader.read(getClass().getResourceAsStream("/report/config/report-config.xml"));
    }

    /**
     * 生成指定项目的所有报表文件
     *
     * @param projectId 项目ID
     */
    public void generateReports(final String projectId, boolean async) {

        List<ReportTask> reportTasks = createReportGenerators(projectId);
        ThreadPoolExecutor pool = async ? executionPool : newBlockingThreadPoolExecutor(10, 10, 100);
        CountDownLatch countDownLatch = new CountDownLatch(reportTasks.size());

        for (final ReportTask reportTask : reportTasks) {
            Runnable runnable = () -> {
                try {
                    String filePath = reportTask.getCategory() + "/" + reportTask.getFilePathWithRange() + ".xlsx";
                    String saveFilePath = getSaveFilePath(projectId, savePath, filePath);

                    LOG.info("开始生成报表 " + reportTask);
                    reportTask.getReportGenerator().generate(projectId, reportTask.getRange(), saveFilePath);

                } catch (Exception e) {
                    LOG.error("生成报表失败", e);
                } finally {
                    countDownLatch.countDown();
                }
            };

            pool.submit(runnable);
        }

        // 满足条件情况下会等待报表生成完毕
        if (isUnitTesting() || !async) {
            try {
                pool.shutdown();
                pool.awaitTermination(1, TimeUnit.DAYS);
            } catch (InterruptedException e) {
                throw new AppException(e);
            }
        }

        try {
            countDownLatch.await(1, TimeUnit.HOURS);
            LOG.info("====项目" + projectId + "报表全部生成完毕。");
        } catch (InterruptedException e) {
            LOG.error("====项目" + projectId + "报表生成超时！");
        }
    }

    private boolean isUnitTesting() {
        return System.getProperty("unit_testing") != null;
    }

    private List<ReportTask> createReportGenerators(String projectId) {

        List<XmlNode> reportSets = reportConfig.getChildren(xmlNode ->
                xmlNode.getTagName().equals("report-set") && xmlNode.getString("id").equals(projectId));

        if (reportSets.isEmpty()) {
            reportSets = reportConfig.getChildren(xmlNode ->
                    xmlNode.getTagName().equals("report-set") && xmlNode.getString("id").equals("default"));
        }

        XmlNode reportSet = reportSets.get(0);
        List<ReportTask> reportTasks = new ArrayList<>();

        try {
            String province = provinceService.getProjectProvince(projectId);
            Context context = new Context().put("projectId", projectId).put("category", "");
            iterateReportSet(context, reportSet, reportTasks, Range.province(province));
        } catch (Exception e) {
            throw new AppException(e);
        }
        return reportTasks;
    }

    private void iterateReportSet(
            Context context, XmlNode xmlNode, List<ReportTask> reportTasks, Range range) throws Exception {

        String projectId = context.get("projectId");
        String category = context.get("category");
        String nodeName = xmlNode.getString("name");
        String nodeRange = xmlNode.getString("range");

        if (xmlNode.getTagName().equals("report-category")) {
            if (nodeRange != null && !(nodeRange.equals("province"))) {
                List<Range> rangeList = rangeService.queryRanges(projectId, nodeRange);
                for (Range _r : rangeList) {
                    for (XmlNode child : xmlNode.getChildren()) {
                        context.put("category", category + "/" + nodeName + "/" + _r.getId());
                        iterateReportSet(context, child, reportTasks, _r);
                    }
                }
            } else {
                for (XmlNode child : xmlNode.getChildren()) {
                    context.put("category", category + "/" + nodeName);
                    iterateReportSet(context, child, reportTasks, range);
                }
            }


        } else if (xmlNode.getTagName().equals("report")) {
            String filename = nodeName;
            String reportClassNameSuffix = xmlNode.getString("class");
            String reportClassNamePrefix = context.get("basePackage");
            String reportClassName = reportClassNamePrefix + reportClassNameSuffix;

            ReportGenerator reportGenerator = (ReportGenerator)
                    this.applicationContext.getBean(Class.forName(reportClassName));

            reportTasks.add(createReportTasks(category, filename, reportGenerator, range));

        } else {  // reportSet

            String basePackage = xmlNode.getString("base");
            for (XmlNode child : xmlNode.getChildren()) {
                context.put("basePackage", basePackage);
                iterateReportSet(context, child, reportTasks, range);
            }

        }
    }

    private ReportTask createReportTasks(
            String category, String filename, ReportGenerator reportGenerator, Range range) {
        return new ReportTask(reportGenerator, category, filename, range);
    }

    /**
     * 生成要保存的报表文件路径
     *
     * @param projectId 项目ID
     * @param savePath  报表根目录
     * @param filePath  报表根目录下的文件路径
     *
     * @return 报表文件路径
     */
    private String getSaveFilePath(String projectId, String savePath, String filePath) {
        String md5 = MD5.digest(projectId);

        return StringUtil.joinPaths(savePath,
                md5.substring(0, 2), md5.substring(2, 4), projectId, filePath);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
