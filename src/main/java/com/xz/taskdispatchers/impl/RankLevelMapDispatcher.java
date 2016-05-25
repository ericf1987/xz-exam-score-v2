package com.xz.taskdispatchers.impl;

import com.xz.bean.ProjectConfig;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.mqreceivers.AggrTask;
import com.xz.services.RangeService;
import com.xz.services.TargetService;
import com.xz.taskdispatchers.TaskDispatcher;
import com.xz.taskdispatchers.TaskDispatcherInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * (description)
 * created at 2016/5/23
 *
 * @author fengye
 */
@TaskDispatcherInfo(taskType = "rank_level_map", dependentTaskType = "rank_level")
@Component
public class RankLevelMapDispatcher extends TaskDispatcher {

    @Autowired
    RangeService rangeService;

    @Autowired
    TargetService targetService;

    @Override
    public void dispatch(String projectId, String aggregationId, ProjectConfig projectConfig) {

        List<Range> ranges = rangeService.queryRanges(projectId, Range.CLASS, Range.SCHOOL);
        List<Target> targets = targetService.queryTargets(projectId, Target.PROJECT, Target.SUBJECT);

        for (Range range : ranges) {
            for (Target target : targets) {
                AggrTask task = createTask(projectId, aggregationId).setRange(range).setTarget(target);
                dispatchTask(task);
            }
        }
    }
}
