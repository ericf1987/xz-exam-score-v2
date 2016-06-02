package com.xz.report;

import com.hyd.simplecache.utils.MD5;
import com.xz.AppException;
import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.ajiaedu.common.xml.XmlNode;
import com.xz.ajiaedu.common.xml.XmlNodeReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
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
    public void generateReports(final String projectId) {

        List<ReportTask> reportTasks = createReportGenerators(projectId);

        for (final ReportTask reportTask : reportTasks) {
            Runnable runnable = () -> {
                try {
                    String filePath = reportTask.getCategory() + "/" + reportTask.getFilename() + ".xlsx";
                    String saveFilePath = getSaveFilePath(projectId, savePath, filePath);
                    reportTask.getReportGenerator().generate(projectId, saveFilePath);
                } catch (Exception e) {
                    LOG.error("生成报表失败", e);
                }
            };

            executionPool.submit(runnable);
        }

        // 单元测试会同步等待直到报表生成完毕
        if (System.getProperty("unit_testing") != null) {
            try {
                executionPool.shutdown();
                executionPool.awaitTermination(1, TimeUnit.DAYS);
            } catch (InterruptedException e) {
                throw new AppException(e);
            }
        }
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
            iterateReportSet(reportSet, "", reportTasks);
        } catch (Exception e) {
            throw new AppException(e);
        }
        return reportTasks;
    }

    private void iterateReportSet(XmlNode node, String category, List<ReportTask> reportTasks) throws Exception {

        if (node.getTagName().equals("report-category")) {
            for (XmlNode child : node.getChildren()) {
                iterateReportSet(child, category + "/" + node.getString("name"), reportTasks);
            }

        } else if (node.getTagName().equals("report")) {
            String filename = node.getString("name");

            ReportGenerator reportGenerator = (ReportGenerator)
                    this.applicationContext.getBean(Class.forName(node.getString("class")));

            reportTasks.add(new ReportTask(reportGenerator, category, filename));

        } else {
            for (XmlNode child : node.getChildren()) {
                iterateReportSet(child, category, reportTasks);
            }

        }
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
                md5.substring(0, 2), md5.substring(2, 4), filePath);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}