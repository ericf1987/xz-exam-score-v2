package com.xz.taskdispatchers;

import com.xz.services.AggregationRoundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * (description)
 * created at 16/05/10
 *
 * @author yiding_he
 */
@Component
public class TaskDispatcherFactory {

    @Autowired
    AggregationRoundService aggregationRoundService;

    private Map<String, TaskDispatcher> dispatcherMap = new HashMap<>();

    public void registerTaskDispatcher(TaskDispatcher taskDispatcher) {
        String taskType = taskDispatcher.getTaskType();
        if (taskType != null) {
            this.dispatcherMap.put(taskType, taskDispatcher);
        }
    }

    public TaskDispatcher getTaskDispatcher(String taskType) {
        return this.dispatcherMap.get(taskType);
    }

    public List<TaskDispatcher> listAvailableDispatchers(String aggregationId) {
        List<String> completedTaskTypes = aggregationRoundService.getCompletedTaskTypes(aggregationId);

        // 去掉已经完成的任务和依赖任务尚未完成的任务
        ArrayList<TaskDispatcher> dispatchers = new ArrayList<>(dispatcherMap.values());
        dispatchers.removeIf(dispatcher -> isDispatcherCompleted(dispatcher, completedTaskTypes));
        dispatchers.removeIf(dispatcher -> !isDependencyCompleted(dispatcher, completedTaskTypes));

        return dispatchers;
    }

    private boolean isDependencyCompleted(TaskDispatcher dispatcher, List<String> completedTaskTypes) {
        return completedTaskTypes.contains(dispatcher.getDependentTaskType());
    }

    private boolean isDispatcherCompleted(TaskDispatcher dispatcher, List<String> completedTaskTypes) {
        return completedTaskTypes.contains(dispatcher.getTaskType());
    }
}
