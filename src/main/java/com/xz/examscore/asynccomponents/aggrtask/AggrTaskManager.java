package com.xz.examscore.asynccomponents.aggrtask;

import com.xz.examscore.asynccomponents.QueueService;
import com.xz.examscore.services.AggregationRoundService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

import static com.xz.ajiaedu.common.concurrent.Executors.newBlockingThreadPoolExecutor;

/**
 * (description)
 * created at 16/05/10
 *
 * @author yiding_he
 */
@Component
public class AggrTaskManager {

    static final Logger LOG = LoggerFactory.getLogger(AggrTaskManager.class);

    private ThreadPoolExecutor executionPool;

    private Map<String, AggrTask> taskInstanceMap = new HashMap<>();

    private boolean stop = false;

    private String lastTaskType = null;

    @Autowired
    AggregationRoundService aggregationRoundService;

    @Autowired
    QueueService queueService;

    @Value("${task.executor.poolsize}")
    private int poolSize;

    @PostConstruct
    public void init() {

        // 单元测试时不侦听任务
        if (System.getProperty("unit_testing") != null) {
            return;
        }

        executionPool = newBlockingThreadPoolExecutor(poolSize, poolSize, 1000);
        startKeeperThread();
        startMonitorThread();

        LOG.info("Message Listener initialized.");
    }

    private void startMonitorThread() {
        Thread monitorThread = new Thread(() -> {
            boolean isLastCheckEmpty = false;
            while (!stop) {
                try {
                    int queueSize = executionPool.getQueue().size();

                    // 长度为 0 只报一次，非 0 每次都报
                    if (queueSize > 0 || !isLastCheckEmpty) {
                        LOG.info("ExecutorService 任务队列长度：" + queueSize);
                    }

                    isLastCheckEmpty = queueSize == 0;
                    Thread.sleep(3000);
                } catch (Exception e) {
                    LOG.error("", e);
                }
            }
        });

        monitorThread.setDaemon(true);
        monitorThread.start();
    }

    private void startKeeperThread() {
        Thread keeperThread = new Thread(() -> {
            while (!stop) {
                try {
                    handleMessages();
                } catch (Exception e) {
                    LOG.error("获取任务失败", e);
                }
            }
        });

        keeperThread.setDaemon(true);
        keeperThread.start();
    }

    @PreDestroy
    public void shutdown() {
        this.stop = true;
        this.executionPool.shutdown();
    }

    private void handleMessages() throws Exception {
        AggrTaskMessage taskInfo = aggregationRoundService.pickTask();

        if (taskInfo != null) {
            if (!taskInfo.getType().equals(lastTaskType)) {
                lastTaskType = taskInfo.getType();
                LOG.info("---- 开始执行队列中的 " + lastTaskType + " 任务。");
            }
            handleCommand(taskInfo, true);
        }
    }

    private void handleCommand(AggrTaskMessage taskInfo, boolean async) {
        String commandType = taskInfo.getType();
        AggrTask aggrTask = taskInstanceMap.get(commandType);
        if (aggrTask != null) {
            if (async) {
                executionPool.submit(() -> aggrTask.taskReceived(taskInfo));
            } else {
                aggrTask.taskReceived(taskInfo);
            }
        }
    }

    public void register(AggrTask aggrTask) {
        AggrTaskMeta info = aggrTask.getClass().getAnnotation(AggrTaskMeta.class);
        taskInstanceMap.put(info.taskType(), aggrTask);
    }

    /**
     * 取一个特定类型的任务并执行（单元测试用）
     *
     * @param taskType 任务类型
     */
    @SuppressWarnings("unused")
    public void pickOneTask(String taskType) {
        AggrTaskMessage taskInfo = aggregationRoundService.pickOneTask(taskType);
        handleCommand(taskInfo, false);
    }
}
