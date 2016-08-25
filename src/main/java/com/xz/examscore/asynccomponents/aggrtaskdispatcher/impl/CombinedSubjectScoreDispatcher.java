package com.xz.examscore.asynccomponents.aggrtaskdispatcher.impl;

import com.xz.examscore.asynccomponents.aggrtaskdispatcher.TaskDispatcher;
import com.xz.examscore.asynccomponents.aggrtaskdispatcher.TaskDispatcherInfo;
import com.xz.examscore.bean.ProjectConfig;
import com.xz.examscore.services.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 有些项目需要对文科理科分数合起来统计
 */
@TaskDispatcherInfo(taskType = "combined_total_score", dependentTaskType = "total_score")
@Component
public class CombinedSubjectScoreDispatcher extends TaskDispatcher {

    @Autowired
    StudentService studentService;

    @Override
    public void dispatch(String projectId, String aggregationId, ProjectConfig projectConfig) {

        if (!projectConfig.isCombineCategorySubjects()) {
            return;
        }

        dispatchTaskForEveryStudent(projectId, aggregationId, studentService);
    }
}
