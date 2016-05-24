package com.xz.taskdispatchers.impl;

import com.xz.bean.ProjectConfig;
import com.xz.bean.Range;
import com.xz.services.RangeService;
import com.xz.services.TargetService;
import com.xz.taskdispatchers.TaskDispatcher;
import com.xz.taskdispatchers.TaskDispatcherInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@TaskDispatcherInfo(taskType = "ranking_level", dependentTaskType = "rank")
public class RankLevelDispatcher extends TaskDispatcher {

    @Autowired
    RangeService rangeService;

    @Autowired
    TargetService targetService;

    @Override
    public void dispatch(String projectId, String aggregationId, ProjectConfig projectConfig) {

        // 对哪些范围计算排名等级
        List<Range> students = rangeService.queryRanges(projectId, Range.STUDENT);

        for (Range student : students) {
            dispatchTask(createTask(projectId, aggregationId).setRange(student));
        }
    }
}
