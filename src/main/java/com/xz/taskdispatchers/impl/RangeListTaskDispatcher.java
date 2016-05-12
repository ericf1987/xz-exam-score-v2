package com.xz.taskdispatchers.impl;

import com.xz.taskdispatchers.TaskDispatcher;
import com.xz.taskdispatchers.TaskDispatcherInfo;
import org.springframework.stereotype.Component;

@TaskDispatcherInfo(taskType = "range_list")
@Component
public class RangeListTaskDispatcher extends TaskDispatcher {

    @Override
    public void dispatch(String projectId, String aggregationId) {
        dispatchTask(createTask(projectId, aggregationId));
    }
}
