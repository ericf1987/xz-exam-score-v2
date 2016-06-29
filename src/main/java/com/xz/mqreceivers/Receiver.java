package com.xz.mqreceivers;

import com.xz.services.AggregationRoundService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;

/**
 * (description)
 * created at 16/05/10
 *
 * @author yiding_he
 */
public abstract class Receiver {

    static final Logger LOG = LoggerFactory.getLogger(Receiver.class);

    @Autowired
    ReceiverManager receiverManager;

    @Autowired
    AggregationRoundService aggregationRoundService;

    @Value("${redis.task.counter.key}")
    private String taskCounterKey;

    @PostConstruct
    public void init() {
        this.receiverManager.registerReceiver(this);
    }

    public void taskReceived(AggrTask task) {
        try {
            runTask(task);
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("执行任务失败", e);
        } finally {
            aggregationRoundService.taskFinished(task);
        }
    }

    protected abstract void runTask(AggrTask aggrTask);
}
