package com.xz.examscore.asynccomponents.importproject;

import com.xz.examscore.asynccomponents.MessageReceiver;
import com.xz.examscore.asynccomponents.aggrtaskdispatcher.DispatchTaskMessage;
import com.xz.examscore.bean.AggregationStatus;
import com.xz.examscore.bean.AggregationType;
import com.xz.examscore.scanner.ScannerDBService;
import com.xz.examscore.services.AggregationService;
import com.xz.examscore.services.ImportProjectService;
import com.xz.examscore.services.ProjectStatusService;
import com.xz.examscore.services.RecordExceptionService;
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

    @Autowired
    RecordExceptionService recordExceptionService;

    @Autowired
    AggregationService aggregationService;

    protected void executeTask(ImportTaskMessage message) {
        String projectId = message.getProjectId();

        AggregationType aggregationType = message.getAggregationType();
        LOG.info("开始导入项目 " + projectId);

        // 导入项目基本信息
        if (message.isImportProjectInfo()) {
            try {
                projectStatusService.setAggregationStatus(projectId, AggregationStatus.Activated);
                aggregationService.importProjectInfo(projectId);
                projectStatusService.setAggregationStatus(projectId, AggregationStatus.Terminated);
            } catch (Exception e) {
                LOG.error("导入项目失败", e);
                projectStatusService.setProjectStatus(projectId, ProjectImporting);
                projectStatusService.setAggregationStatus(projectId, AggregationStatus.Terminated);
                recordExceptionService.recordException(projectId, ProjectImporting, e, "导入项目基础信息出现异常，请检查！");
                return;
            }
        }

        // 导入网阅成绩信息
        if (message.isImportProjectScore()) {
            try {
                projectStatusService.setAggregationStatus(projectId, AggregationStatus.Activated);
                aggregationService.importScannerScore(projectId);
                projectStatusService.setAggregationStatus(projectId, AggregationStatus.Terminated);
            } catch (Exception e) {
                LOG.error("导入网阅分数失败", e);
                projectStatusService.setProjectStatus(projectId, ScoreImporting);
                projectStatusService.setAggregationStatus(projectId, AggregationStatus.Terminated);
                recordExceptionService.recordException(projectId, ScoreImporting, e, "导入分数出现异常，请检查！");
                return;
            }
        }

        // 如果需要，则发送开始统计命令
        if (message.isInAggrProcess()) {
            boolean generateReport = message.isGenerateReport();
            boolean exportScore = message.isExportScore();

            getQueueService().addToQueue(DispatchTaskList,
                    new DispatchTaskMessage(projectId, aggregationType, generateReport, exportScore));
        }
    }

}
