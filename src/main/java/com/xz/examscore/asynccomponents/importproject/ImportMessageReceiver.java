package com.xz.examscore.asynccomponents.importproject;

import com.xz.examscore.asynccomponents.MessageReceiver;
import com.xz.examscore.asynccomponents.aggrtaskdispatcher.DispatchTaskMessage;
import com.xz.examscore.bean.AggregationType;
import com.xz.examscore.scanner.ScannerDBService;
import com.xz.examscore.services.ImportProjectService;
import com.xz.examscore.services.ProjectStatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.xz.examscore.asynccomponents.QueueType.DispatchTaskList;
import static com.xz.examscore.bean.ProjectStatus.*;

/**
 * 获取导入项目消息并执行
 *
 * @author yiding_he
 */
@Component
public class ImportMessageReceiver extends MessageReceiver<ImportTaskMessage> {

    private static final Logger LOG = LoggerFactory.getLogger(ImportMessageReceiver.class);

    @Autowired
    ImportProjectService importProjectService;

    @Autowired
    ProjectStatusService projectStatusService;

    @Autowired
    ScannerDBService scannerDBService;

    protected void executeTask(ImportTaskMessage message) {
        String projectId = message.getProjectId();
        AggregationType aggregationType = message.getAggregationType();
        LOG.info("开始导入项目 " + projectId);

        try {
            // 导入项目基本信息
            if (message.isImportProjectInfo()) {
                projectStatusService.setProjectStatus(projectId, ProjectImporting);
                importProjectService.importProject(projectId, true);
                projectStatusService.setProjectStatus(projectId, ProjectImported);
            }

            // 导入网阅成绩信息
            if (message.isImportProjectScore()) {
                projectStatusService.setProjectStatus(projectId, ScoreImporting);
                scannerDBService.importProjectScore(projectId);
                projectStatusService.setProjectStatus(projectId, ScoreImported);
            }

            // 如果需要，则发送开始统计命令
            if (message.isInAggrProcess()) {
                boolean generateReport = message.isGenerateReport();

                getQueueService().addToQueue(DispatchTaskList,
                        new DispatchTaskMessage(projectId, aggregationType, generateReport));
            }
        } catch (Exception e) {
            LOG.error("导入项目失败", e);
            projectStatusService.setProjectStatus(projectId, AggregationFailed);
        }
    }

}
