package com.xz.services;

import com.xz.taskdispatchers.TaskDispatcher;
import com.xz.taskdispatchers.TaskDispatcherFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * 处理统计任务的进度和轮次安排
 *
 * @author yiding_he
 */
@Service
public class AggregationService {

    static final Logger LOG = LoggerFactory.getLogger(AggregationService.class);

    @Autowired
    TaskDispatcherFactory taskDispatcherFactory;

    @Autowired
    AggregationRoundService aggregationRoundService;

    public void startAggregation(final String projectId, boolean async) {
        Runnable runnable = () -> {
            try {
                runAggregation0(projectId);
            } catch (Exception e) {
                LOG.error("分发任务失败", e);
            }
        };

        if (async) {
            Thread thread = new Thread(runnable);
            thread.setDaemon(true);
            thread.start();
        } else {
            runnable.run();
        }
    }

    private void runAggregation0(String projectId) {
        String aggregationId = UUID.randomUUID().toString();
        LOG.info("开始对项目{}的统计，本次统计ID={}", projectId, aggregationId);
        List<TaskDispatcher> dispatcherList;
        int round = 1;

        do {
            dispatcherList = createDispatchers(aggregationId);
            LOG.info("对项目{}的第{}轮统计(ID={})任务：{}", projectId, round, aggregationId, dispatcherList);
            runDispatchers(projectId, aggregationId, dispatcherList);
            LOG.info("对项目{}的第{}轮统计(ID={})任务分发完毕", projectId, round, aggregationId);
            waitForTaskCompletion(aggregationId);
            LOG.info("对项目{}的第{}轮统计(ID={})任务执行完毕。", projectId, round, aggregationId);

            round += 1;
        } while (!dispatcherList.isEmpty());

        LOG.info(" ==> 对项目{}的统计全部结束，本次统计ID={}", projectId, aggregationId);
    }

    private void waitForTaskCompletion(String aggregationId) {
        aggregationRoundService.waitForRoundCompletion(aggregationId);
    }

    private List<TaskDispatcher> createDispatchers(String aggregationId) {
        return taskDispatcherFactory.listAvailableDispatchers(aggregationId);
    }

    private void runDispatchers(String projectId, String aggregationId, List<TaskDispatcher> dispatcherList) {
        for (TaskDispatcher dispatcher : dispatcherList) {
            dispatcher.dispatch(projectId, aggregationId);
        }
    }
}
