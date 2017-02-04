package com.xz.examscore.asynccomponents.aggrtask;

import com.xz.examscore.services.AggregationRoundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;

/**
 * (description)
 * created at 16/05/10
 *
 * @author yiding_he
 */
public abstract class AggrTask {

    @Autowired
    private AggrTaskManager aggrTaskManager;

    @Autowired
    private AggregationRoundService aggregationRoundService;

    @Value("${redis.task.counter.key}")
    private String taskCounterKey;

    @PostConstruct
    public void init() {
        this.aggrTaskManager.register(this);
    }

    void taskReceived(AggrTaskMessage task) {

        // 异常会在 com.xz.examscore.asynccomponents.MessageReceiver.executeTaskSafe() 方法截获

        try {
            runTask(task);
        } finally {
            aggregationRoundService.taskFinished(task);
        }
    }

    protected abstract void runTask(AggrTaskMessage taskInfo);
}
