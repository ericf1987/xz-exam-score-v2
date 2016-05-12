package com.xz.services;

import com.xz.taskdispatchers.TaskDispatcher;
import com.xz.taskdispatchers.TaskDispatcherFactory;
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

    @Autowired
    TaskDispatcherFactory taskDispatcherFactory;

    @Autowired
    AggregationRoundService aggregationRoundService;

    public void startAggregation(final String projectId, boolean async) {
        Runnable runnable = () -> {
            String aggregationId = UUID.randomUUID().toString();
            List<TaskDispatcher> dispatcherList;

            do {
                dispatcherList = createDispatchers(aggregationId);
                runDispatchers(projectId, aggregationId, dispatcherList);
                waitForTaskCompletion(aggregationId);
            } while (!dispatcherList.isEmpty());
        };

        if (async) {
            Thread thread = new Thread(runnable);
            thread.setDaemon(true);
            thread.start();
        } else {
            runnable.run();
        }
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
