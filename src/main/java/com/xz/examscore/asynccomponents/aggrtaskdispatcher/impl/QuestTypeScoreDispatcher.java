package com.xz.examscore.asynccomponents.aggrtaskdispatcher.impl;

import com.xz.examscore.asynccomponents.aggrtaskdispatcher.TaskDispatcher;
import com.xz.examscore.asynccomponents.aggrtaskdispatcher.TaskDispatcherInfo;
import com.xz.examscore.bean.ProjectConfig;
import com.xz.examscore.services.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 统计单个学生题型得分
 */
@Component
@TaskDispatcherInfo(taskType = "quest_type_score", isAdvanced = true)
public class QuestTypeScoreDispatcher extends TaskDispatcher {

    @Autowired
    StudentService studentService;

    @Override
    public void dispatch(String projectId, String aggregationId, ProjectConfig projectConfig) {
        dispatchTaskForEveryStudent(projectId, aggregationId);
    }
}
