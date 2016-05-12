package com.xz.mqreceivers;

import com.alibaba.fastjson.JSON;
import com.xz.services.AggregationRoundService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * (description)
 * created at 16/05/10
 *
 * @author yiding_he
 */
@Component
public class ReceiverManager {

    static final Logger LOG = LoggerFactory.getLogger(ReceiverManager.class);

    private ExecutorService executionPool = Executors.newFixedThreadPool(10);

    private Map<String, Receiver> receiverMap = new HashMap<>();

    private boolean stop = false;

    @Autowired
    AggregationRoundService aggregationRoundService;

    @PostConstruct
    public void init() {

        // 单元测试时不侦听任务
        if (System.getProperty("unit_testing") != null) {
            return;
        }

        Thread keeperThread = new Thread(() -> {
            while (!stop) {
                try {
                    handleMessages();
                } catch (Exception e) {
                    LOG.error("", e);
                }
            }
        });

        keeperThread.setDaemon(true);
        keeperThread.start();

        LOG.info("Message Listener initialized.");
    }

    @PreDestroy
    public void shutdown() {
        this.stop = true;
    }

    private void handleMessages() throws Exception {
        String taskJson = aggregationRoundService.pickTask();

        if (taskJson != null) {
            AggrTask aggrTask = JSON.parseObject(taskJson, AggrTask.class);
            handleCommand(aggrTask, true);
        }
    }

    private void handleCommand(AggrTask aggrTask, boolean async) {
        String commandType = aggrTask.getType();
        Receiver receiver = receiverMap.get(commandType);
        if (receiver != null) {
            if (async) {
                executionPool.submit(() -> receiver.taskReceived(aggrTask));
            } else {
                receiver.taskReceived(aggrTask);
            }
        }
    }

    public void registerReceiver(Receiver receiver) {
        ReceiverInfo info = receiver.getClass().getAnnotation(ReceiverInfo.class);
        receiverMap.put(info.taskType(), receiver);
    }

    /**
     * 取一个特定类型的任务并执行（单元测试用）
     *
     * @param taskType 任务类型
     */
    public void pickOneTask(String taskType) {
        AggrTask aggrTask = aggregationRoundService.pickOneTask(taskType);
        handleCommand(aggrTask, false);
    }
}
