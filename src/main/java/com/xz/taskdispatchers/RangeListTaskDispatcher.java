package com.xz.taskdispatchers;

import org.springframework.stereotype.Component;

@TaskDispatcherInfo(taskType = "range_list")
@Component
public class RangeListTaskDispatcher extends TaskDispatcher {

    @Override
    public void dispatch(String projectId) {
        dispatchTask(createTask(projectId));
    }
}
