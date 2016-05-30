package com.xz.report;

import com.hyd.simplecache.utils.MD5;
import com.xz.ajiaedu.common.lang.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import static com.xz.ajiaedu.common.concurrent.Executors.newBlockingThreadPoolExecutor;

/**
 * (description)
 * created at 16/05/30
 *
 * @author yiding_he
 */
@Component
public class ReportManager {

    static final Logger LOG = LoggerFactory.getLogger(ReportManager.class);

    private ThreadPoolExecutor executionPool;

    private List<ReportGenerator> reportGenerators = new ArrayList<>();

    @Value("${report.generator.poolsize}")
    private int poolSize;

    @Value("${report.generator.savepath}")
    private String savePath;

    @PostConstruct
    public void init() {
        executionPool = newBlockingThreadPoolExecutor(poolSize, poolSize, 100);
    }

    public void registerReport(ReportGenerator reportGenerator) {
        if (!reportGenerators.contains(reportGenerator)) {
            reportGenerators.add(reportGenerator);
        }
    }

    /**
     * 生成指定项目的所有报表文件
     *
     * @param projectId 项目ID
     */
    public void generateReports(final String projectId) {
        for (final ReportGenerator reportGenerator : reportGenerators) {
            Runnable runnable = () -> {
                try {
                    ReportInfo reportInfo = reportGenerator.getInfo();
                    String saveFilePath = getSaveFilePath(projectId, savePath, reportInfo);

                    reportGenerator.generate(projectId, saveFilePath);
                } catch (Exception e) {
                    LOG.error("生成报表失败", e);
                }
            };

            executionPool.submit(runnable);
        }
    }

    /**
     * 生成要保存的报表文件路径
     *
     * @param projectId  项目ID
     * @param savePath   报表根目录
     * @param reportInfo 报表信息
     *
     * @return 报表文件路径
     */
    private String getSaveFilePath(String projectId, String savePath, ReportInfo reportInfo) {
        String md5 = MD5.digest(projectId);

        return StringUtil.joinPaths(savePath,
                md5.substring(0, 2), md5.substring(2, 4), reportInfo.category(), reportInfo.fileName());
    }

}
