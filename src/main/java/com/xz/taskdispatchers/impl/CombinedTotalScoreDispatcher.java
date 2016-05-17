package com.xz.taskdispatchers.impl;

import com.xz.bean.ProjectConfig;
import com.xz.taskdispatchers.TaskDispatcher;
import com.xz.taskdispatchers.TaskDispatcherInfo;
import org.springframework.stereotype.Component;

/**
 * 有些项目需要对文科理科分数合起来统计
 */
@TaskDispatcherInfo(taskType = "combined_total_score", dependentTaskType = "total_score")
@Component
public class CombinedTotalScoreDispatcher extends TaskDispatcher {

    @Override
    public void dispatch(String projectId, String aggregationId, ProjectConfig projectConfig) {

    }
}
