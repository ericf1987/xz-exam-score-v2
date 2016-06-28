package com.xz.taskdispatchers.impl;

import com.xz.bean.ProjectConfig;
import com.xz.services.StudentService;
import com.xz.taskdispatchers.TaskDispatcher;
import com.xz.taskdispatchers.TaskDispatcherInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 统计单个学生题型得分
 */
@Component
@TaskDispatcherInfo(taskType = "quest_type_score")
public class QuestTypeScoreDispatcher extends TaskDispatcher {

    @Autowired
    StudentService studentService;

    @Override
    public void dispatch(String projectId, String aggregationId, ProjectConfig projectConfig) {
        dispatchTaskForEveryStudent(projectId, aggregationId, studentService);
    }
}
