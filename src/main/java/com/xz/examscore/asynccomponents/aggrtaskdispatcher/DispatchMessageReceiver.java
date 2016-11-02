package com.xz.examscore.asynccomponents.aggrtaskdispatcher;

import com.xz.examscore.asynccomponents.MessageReceiver;
import com.xz.examscore.asynccomponents.QueueService;
import com.xz.examscore.asynccomponents.QueueType;
import com.xz.examscore.asynccomponents.report.ReportTaskMessage;
import com.xz.examscore.bean.AggregationStatus;
import com.xz.examscore.bean.AggregationType;
import com.xz.examscore.services.AggregationService;
import com.xz.examscore.services.ProjectStatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 获取统计项目消息并执行
 *
 * @author yiding_he
 */
@Component
public class DispatchMessageReceiver extends MessageReceiver<DispatchTaskMessage> {

    static final Logger LOG = LoggerFactory.getLogger(DispatchMessageReceiver.class);

    @Autowired
    AggregationService aggregationService;

    @Autowired
    QueueService queueService;

    @Autowired
    ProjectStatusService projectStatusService;

    @Override
    protected void executeTask(DispatchTaskMessage message) {

        ////////////////////////////////////////////////////////////// 开始统计(每个项目一个 message)

        String projectId = message.getProjectId();

        //获取到考试执行任务以后，将考试任务在数据库中标记为执行中
        projectStatusService.setAggregationStatus(projectId, AggregationStatus.Activated);

        AggregationType aggregationType = message.getAggregationType();
        aggregationService.runAggregationOnly(projectId, aggregationType);

        ////////////////////////////////////////////////////////////// 统计完成

        if (message.isExportScore()) {
            projectStatusService.setAggregationStatus(projectId, AggregationStatus.Activated);
            aggregationService.exportScore(projectId);
            projectStatusService.setAggregationStatus(projectId, AggregationStatus.Terminated);
        }
        if (message.isGenerateReport()) {
            projectStatusService.setAggregationStatus(projectId, AggregationStatus.Activated);
            queueService.addToQueue(QueueType.ReportTaskList, new ReportTaskMessage(projectId));
            projectStatusService.setAggregationStatus(projectId, AggregationStatus.Terminated);
        }
    }
}
