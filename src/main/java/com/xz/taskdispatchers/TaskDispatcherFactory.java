package com.xz.taskdispatchers;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * (description)
 * created at 16/05/10
 *
 * @author yiding_he
 */
@Component
public class TaskDispatcherFactory {

    private Map<String, TaskDispatcher> dispatcherMap = new HashMap<>();

    public void registerTaskDispatcher(TaskDispatcher taskDispatcher) {
        TaskDispatcherInfo dispatcherInfo = taskDispatcher.getClass().getAnnotation(TaskDispatcherInfo.class);
        if (dispatcherInfo == null) {
            return;
        }

        String taskType = dispatcherInfo.taskType();
        this.dispatcherMap.put(taskType, taskDispatcher);
    }

    public TaskDispatcher getTaskDispatcher(String taskType) {
        return this.dispatcherMap.get(taskType);
    }
}
