package com.xz.taskdispatchers.impl;

import com.xz.bean.ProjectConfig;
import com.xz.taskdispatchers.TaskDispatcher;
import com.xz.taskdispatchers.TaskDispatcherInfo;
import org.springframework.stereotype.Component;

/**
 * Created by fengye on 2016/5/23.
 */
@TaskDispatcherInfo(taskType = "ranking_level_hz", dependentTaskType = "ranking_level")
@Component
public class RankLevelHZTaskDispatcher extends TaskDispatcher{

    @Override
    public void dispatch(String projectId, String aggregationId, ProjectConfig projectConfig) {
        //查询出总分表中学生的班级和班级等第成绩
        dispatchTask(createTask(projectId, aggregationId));
    }
}
