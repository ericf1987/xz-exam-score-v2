package com.xz.taskdispatchers;

import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.services.RangeService;
import com.xz.services.TargetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@TaskDispatcherInfo(taskType = "minmax")
@Component
public class MinMaxTaskDispatcher extends TaskDispatcher {

    @Autowired
    TargetService targetService;

    @Autowired
    RangeService rangeService;

    @Override
    public void dispatch(String projectId) {

        // 题目的最高最低分统计在 mapreduce 中完成
        List<Target> targets = targetService.queryTargets(projectId,
                Target.PROJECT, Target.SUBJECT);

        List<Range> ranges = rangeService.queryRanges(projectId,
                Range.CLASS, Range.SCHOOL, Range.AREA, Range.CITY, Range.PROVINCE);

        for (Target target : targets) {
            for (Range range : ranges) {
                dispatchTask(createTask(projectId).setTarget(target).setRange(range));
            }
        }
    }
}
