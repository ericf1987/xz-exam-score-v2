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

@TaskDispatcherInfo(taskType = "score_map", dependentTaskType = "combined_total_score")
@Component
public class ScoreMapDispatcher extends TaskDispatcher {

    @Autowired
    RangeService rangeService;

    @Autowired
    TargetService targetService;

    @Override
    public void dispatch(String projectId, String aggregationId, ProjectConfig projectConfig) {

        // 对哪些范围进行排名
        List<Range> ranges = rangeService.queryRanges(projectId,
                Range.PROVINCE, Range.CITY, Range.AREA, Range.SCHOOL, Range.CLASS);

        // 对哪些分数进行排名
        List<Target> targets = targetService.queryTargets(projectId,
                Target.QUEST, Target.SUBJECT, Target.SUBJECT_OBJECTIVE, Target.PROJECT, Target.POINT, Target.QUEST_TYPE);

        // 如果项目需要对文综理综进行整合（考试本身没有这两个科目），则额外
        // 添加文综理综的排名统计（总分统计在 CombinedSubjectScoreDispatcher 里已经做了）
        if (projectConfig.isCombineCategorySubjects()) {
            targets.add(Target.subject("004005006"));   // 理综
            targets.add(Target.subject("007008009"));   // 文综
        }

        for (Target target : targets) {
            for (Range range : ranges) {
                dispatchTask(createTask(projectId, aggregationId).setRange(range).setTarget(target));
            }
        }
    }
}
