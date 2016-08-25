package com.xz.examscore.asynccomponents.aggrtaskdispatcher;

import com.xz.examscore.asynccomponents.MessageReceiver;
import com.xz.examscore.bean.AggregationType;
import com.xz.examscore.services.AggregationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * (description)
 * created at 16/08/25
 *
 * @author yiding_he
 */
@Component
public class DispatchMessageReceiver extends MessageReceiver<DispatchTaskMessage> {

    @Autowired
    AggregationService aggregationService;

    @Override
    protected void executeTask(DispatchTaskMessage message) {
        String projectId = message.getProjectId();
        AggregationType aggregationType = message.getAggregationType();
        aggregationService.runAggregationOnly(projectId, aggregationType);
        // todo 统计完毕，发送生成报表任务
    }
}
