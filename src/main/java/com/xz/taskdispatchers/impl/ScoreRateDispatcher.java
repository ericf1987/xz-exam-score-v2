package com.xz.taskdispatchers.impl;

import com.xz.bean.ProjectConfig;
import com.xz.bean.Range;
import com.xz.bean.SubjectObjective;
import com.xz.bean.Target;
import com.xz.services.RangeService;
import com.xz.services.TargetService;
import com.xz.taskdispatchers.TaskDispatcher;
import com.xz.taskdispatchers.TaskDispatcherInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@TaskDispatcherInfo(taskType = "score_rate", dependentTaskType = "total_score")
@Component
public class ScoreRateDispatcher extends TaskDispatcher {

    @Autowired
    RangeService rangeService;

    @Autowired
    TargetService targetService;

    @Override
    public void dispatch(String projectId, String aggregationId, ProjectConfig projectConfig) {

        // 一般分数统计
        // 统计得分率的范围：总体、学校、班级
        // 统计得分率的目标：题型、主观题
        List<Range> ranges = rangeService.queryRanges(projectId, Range.CLASS, Range.SCHOOL, Range.PROVINCE);
        List<Target> targets = targetService.queryTargets(projectId, Target.QUEST_TYPE, Target.SUBJECT_OBJECTIVE);

        for (Range range : ranges) {
            for (Target target : targets) {

                // 客观题不参与得分率统计
                if (isObjective(target)) {
                    continue;
                }

                dispatchTask(createTask(projectId, aggregationId).setRange(range).setTarget(target));
            }
        }

        // 知识点得分率统计
        ranges = rangeService.queryRanges(projectId, Range.STUDENT);
        targets = targetService.queryTargets(projectId, Target.POINT);
        for (Range range : ranges) {
            for (Target target : targets) {
                dispatchTask(createTask(projectId, aggregationId).setRange(range).setTarget(target));
            }
        }

        // [知识点-能力层级]得分率统计
        ranges = rangeService.queryRanges(projectId, Range.CLASS, Range.SCHOOL);
        targets = targetService.queryTargets(projectId, Target.POINT_LEVEL);
        for (Range range : ranges) {
            for (Target target : targets) {
                dispatchTask(createTask(projectId, aggregationId).setRange(range).setTarget(target));
            }
        }

    }

    private boolean isObjective(Target target) {
        return target.match(Target.SUBJECT_OBJECTIVE) && target.getId(SubjectObjective.class).isObjective();
    }
}
