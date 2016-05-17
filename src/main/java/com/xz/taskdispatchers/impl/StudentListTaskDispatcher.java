package com.xz.taskdispatchers.impl;

import com.xz.bean.ProjectConfig;
import com.xz.taskdispatchers.TaskDispatcher;
import com.xz.taskdispatchers.TaskDispatcherInfo;
import org.springframework.stereotype.Component;

@TaskDispatcherInfo(taskType = "student_list")
@Component
public class StudentListTaskDispatcher extends TaskDispatcher {

    @Override
    public void dispatch(String projectId, String aggregationId, ProjectConfig projectConfig) {
        dispatchTask(createTask(projectId, aggregationId));
    }
}
