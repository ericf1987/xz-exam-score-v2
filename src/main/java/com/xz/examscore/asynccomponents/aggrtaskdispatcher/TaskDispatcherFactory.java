package com.xz.examscore.asynccomponents.aggrtaskdispatcher;

import com.xz.examscore.bean.AggregationType;
import com.xz.examscore.services.AggregationRoundService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOG = LoggerFactory.getLogger(TaskDispatcherFactory.class);

    @Autowired
    private AggregationRoundService aggregationRoundService;

    // 保存所有统计对象
    private Map<String, TaskDispatcher> dispatcherMap = new HashMap<>();

    // 注册 Dispatcher 对象
    void registerTaskDispatcher(TaskDispatcher taskDispatcher) {
        String taskType = taskDispatcher.getTaskType();

        if (taskType != null) {
            this.dispatcherMap.put(taskType, taskDispatcher);
            LOG.info("注册 TaskDispatcher: " + taskType + " = " + taskDispatcher.getClass().getSimpleName());
        }
    }

    public TaskDispatcher getTaskDispatcher(String taskType) {
        return this.dispatcherMap.get(taskType);
    }

    //////////////////////////////////////////////////////////////

    public List<TaskDispatcher> listAvailableDispatchers(String aggregationId, AggregationType aggregationType) {
        List<String> completedTaskTypes = aggregationRoundService.getCompletedTaskTypes(aggregationId);

        // 去掉已经完成的任务和依赖任务尚未完成的任务
        ArrayList<TaskDispatcher> dispatchers = new ArrayList<>(dispatcherMap.values());
        dispatchers.removeIf(dispatcher -> isDispatcherCompleted(dispatcher, completedTaskTypes));
        dispatchers.removeIf(dispatcher -> !isDependencyCompleted(dispatcher, completedTaskTypes));

        // 去掉个性化需求统计任务
        dispatchers.removeIf(this::isCustomized);

        if (aggregationType == AggregationType.Basic) {
            dispatchers.removeIf(this::isAdvanced);
        } else if (aggregationType == AggregationType.Advanced) {
            dispatchers.removeIf(dispatcher -> !isAdvanced(dispatcher));
        }

        return dispatchers;
    }

    private boolean isAdvanced(TaskDispatcher dispatcher) {
        return dispatcher.getInfo().isAdvanced();
    }

    private boolean isCustomized(TaskDispatcher dispatcher) {
        return dispatcher.getInfo().isCustomized();
    }

    // 没有依赖任务亦可视为依赖任务已完成
    private boolean isDependencyCompleted(TaskDispatcher dispatcher, List<String> completedTaskTypes) {
        String dependentTaskType = dispatcher.getDependentTaskType();
        return dependentTaskType == null || completedTaskTypes.contains(dependentTaskType);
    }

    private boolean isDispatcherCompleted(TaskDispatcher dispatcher, List<String> completedTaskTypes) {
        return completedTaskTypes.contains(dispatcher.getTaskType());
    }
}
