package com.xz.examscore.asynccomponents.aggrtaskdispatcher.impl;

import com.xz.examscore.asynccomponents.aggrtaskdispatcher.TaskDispatcher;
import com.xz.examscore.asynccomponents.aggrtaskdispatcher.TaskDispatcherInfo;
import com.xz.examscore.bean.ProjectConfig;
import com.xz.examscore.services.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 等第统计
 */
@Component
@TaskDispatcherInfo(taskType = "rank_level", dependentTaskType = "score_map")
public class RankLevelDispatcher extends TaskDispatcher {

    @Autowired
    StudentService studentService;

    @Override
    public void dispatch(String projectId, String aggregationId, ProjectConfig projectConfig) {
        dispatchTaskForEveryStudent(projectId, aggregationId);
    }
}
