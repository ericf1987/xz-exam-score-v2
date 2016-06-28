package com.xz.taskdispatchers.impl;

import com.xz.bean.ProjectConfig;
import com.xz.services.StudentService;
import com.xz.taskdispatchers.TaskDispatcher;
import com.xz.taskdispatchers.TaskDispatcherInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@TaskDispatcherInfo(taskType = "rank_level", dependentTaskType = "score_map")
public class RankLevelDispatcher extends TaskDispatcher {

    @Autowired
    StudentService studentService;

    @Override
    public void dispatch(String projectId, String aggregationId, ProjectConfig projectConfig) {
        dispatchTaskForEveryStudent(projectId, aggregationId, studentService);
    }
}
