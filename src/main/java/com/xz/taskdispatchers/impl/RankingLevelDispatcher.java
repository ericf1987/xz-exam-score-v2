package com.xz.taskdispatchers.impl;

import com.xz.bean.ProjectConfig;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.services.RangeService;
import com.xz.services.TargetService;
import com.xz.taskdispatchers.TaskDispatcher;
import com.xz.taskdispatchers.TaskDispatcherInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@TaskDispatcherInfo(taskType = "ranking_level", dependentTaskType = "rank")
public class RankingLevelDispatcher extends TaskDispatcher {

    @Autowired
    RangeService rangeService;

    @Autowired
    TargetService targetService;

    @Override
    public void dispatch(String projectId, String aggregationId, ProjectConfig projectConfig) {

        // 对哪些范围计算排名等级
        List<Range> ranges = rangeService.queryRanges(projectId, Range.CLASS, Range.SCHOOL);

        // 对哪些分数计算排名等级
        List<Target> targets = targetService.queryTargets(projectId, Target.SUBJECT, Target.PROJECT);

        // 需要合计文科理科成绩的项目
        if (projectConfig.isCombineCategorySubjects()) {
            targets.add(Target.subject("004005006"));
            targets.add(Target.subject("007008009"));
        }

        for (Range range : ranges) {
            for (Target target : targets) {
                dispatchTask(createTask(projectId, aggregationId).setRange(range).setTarget(target));
            }
        }

    }
}
