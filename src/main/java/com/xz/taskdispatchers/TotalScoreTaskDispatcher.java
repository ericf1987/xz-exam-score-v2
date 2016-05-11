package com.xz.taskdispatchers;

import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.services.RangeService;
import com.xz.services.TargetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@TaskDispatcherInfo(taskType = "total_score")
@Component
public class TotalScoreTaskDispatcher extends TaskDispatcher {

    @Autowired
    RangeService rangeService;

    @Autowired
    TargetService targetService;

    @Override
    public void dispatch(String projectId) {

        List<Range> ranges = rangeService.queryRanges(projectId,
                Range.STUDENT, Range.CLASS, Range.SCHOOL, Range.AREA, Range.CITY, Range.PROVINCE);

        List<Target> targets = targetService.queryTargets(projectId,
                Target.QUEST, Target.SUBJECT, Target.PROJECT, Target.POINT, Target.QUEST_TYPE);

        for (Range range : ranges) {
            for (Target target : targets) {
                dispatchTask(createTask(projectId).setRange(range).setTarget(target));
            }
        }
    }
}
