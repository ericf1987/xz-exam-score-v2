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

/**
 * 分数分段人数统计
 *
 * @author yiding_he
 */
@Component
@TaskDispatcherInfo(taskType = "score_segment", dependentTaskType = "total_score")
public class ScoreSegmentDispatcher extends TaskDispatcher {

    @Autowired
    RangeService rangeService;

    @Autowired
    TargetService targetService;

    @Override
    public void dispatch(String projectId, String aggregationId, ProjectConfig projectConfig) {
        List<Range> ranges = rangeService.queryRanges(projectId, Range.CLASS, Range.SCHOOL, Range.PROVINCE);
        List<Target> targets = targetService.queryTargets(projectId, Target.PROJECT, Target.SUBJECT);

        for (Target target : targets) {
            for (Range range : ranges) {
                dispatchTask(createTask(projectId, aggregationId).setTarget(target).setRange(range));
            }
        }
    }
}