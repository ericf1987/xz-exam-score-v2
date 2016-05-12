package com.xz.taskdispatchers.impl;

import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.services.RangeService;
import com.xz.services.TargetService;
import com.xz.taskdispatchers.TaskDispatcher;
import com.xz.taskdispatchers.TaskDispatcherInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@TaskDispatcherInfo(taskType = "total_score")
@Component
public class TotalScoreTaskDispatcher extends TaskDispatcher {

    static final Logger LOG = LoggerFactory.getLogger(TotalScoreTaskDispatcher.class);

    @Autowired
    RangeService rangeService;

    @Autowired
    TargetService targetService;

    @Override
    public void dispatch(String projectId, String aggregationId) {

        List<Range> ranges = rangeService.queryRanges(projectId,
                Range.STUDENT, Range.CLASS, Range.SCHOOL, Range.AREA, Range.CITY, Range.PROVINCE);

        List<Target> targets = targetService.queryTargets(projectId,
                Target.QUEST, Target.SUBJECT, Target.SUBJECT_OBJECTIVE, Target.PROJECT, Target.POINT, Target.QUEST_TYPE);

        int counter = 0;
        for (Range range : ranges) {
            for (Target target : targets) {

                // 单个题目的分数无需统计，就是在详情数据 score 集合里面
                if (range.match(Range.STUDENT) && target.match(Target.QUEST)) {
                    continue;
                }

                dispatchTask(createTask(projectId, aggregationId).setRange(range).setTarget(target));
                counter++;
                if (counter % 100 == 0) {
                    LOG.info("为项目 " + projectId + " 的 total_score 统计发布了 " + counter + " 个任务");
                }
            }
        }
        LOG.info("最终为项目 " + projectId + " 的 total_score 统计发布了 " + counter + " 个任务");
    }
}
