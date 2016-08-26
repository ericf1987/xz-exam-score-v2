package com.xz.examscore.asynccomponents.aggrtaskdispatcher;

import com.xz.examscore.asynccomponents.MessageReceiver;
import com.xz.examscore.asynccomponents.QueueService;
import com.xz.examscore.asynccomponents.QueueType;
import com.xz.examscore.asynccomponents.report.ReportTaskMessage;
import com.xz.examscore.bean.AggregationType;
import com.xz.examscore.services.AggregationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 获取统计项目消息并执行
 *
 * @author yiding_he
 */
@Component
public class DispatchMessageReceiver extends MessageReceiver<DispatchTaskMessage> {

    @Autowired
    AggregationService aggregationService;

    @Autowired
    QueueService queueService;

    @Override
    protected void executeTask(DispatchTaskMessage message) {
        String projectId = message.getProjectId();
        AggregationType aggregationType = message.getAggregationType();
        aggregationService.runAggregationOnly(projectId, aggregationType);

        if (message.isGenerateReport()) {
            queueService.addToQueue(QueueType.ReportTaskList, new ReportTaskMessage(projectId));
        }
    }
}
