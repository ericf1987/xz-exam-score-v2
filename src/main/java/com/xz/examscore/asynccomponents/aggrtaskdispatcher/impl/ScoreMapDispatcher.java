package com.xz.examscore.asynccomponents.aggrtaskdispatcher.impl;

import com.xz.examscore.asynccomponents.aggrtaskdispatcher.TaskDispatcher;
import com.xz.examscore.asynccomponents.aggrtaskdispatcher.TaskDispatcherInfo;
import com.xz.examscore.bean.ProjectConfig;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.RangeService;
import com.xz.examscore.services.TargetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@TaskDispatcherInfo(taskType = "score_map", dependentTaskType = "combined_total_score")
@Component
public class ScoreMapDispatcher extends TaskDispatcher {
    static final Logger LOG = LoggerFactory.getLogger(ScoreLevelMapDispatcher.class);

    @Autowired
    RangeService rangeService;

    @Autowired
    TargetService targetService;

    @Override
    public void dispatch(String projectId, String aggregationId, ProjectConfig projectConfig, Map<String, List<Range>> rangesMap) {

        // 对哪些范围进行排名
        String[] rangeKeys = new String[]{
                Range.CLASS, Range.SCHOOL, Range.PROVINCE
        };

        List<Range> ranges = fetchRanges(rangeKeys, rangesMap);
        // 对哪些分数进行排名
        // 对分数进行排名是因为要计算题目的区分度
        List<Target> targets = targetService.queryTargets(projectId, Target.SUBJECT, Target.SUBJECT_COMBINATION, Target.PROJECT, Target.QUEST);

        // 如果项目需要对文综理综进行整合（考试本身没有这两个科目），则额外
        // 添加文综理综的排名统计（总分统计在 CombinedSubjectScoreDispatcher 里已经做了）
        if (projectConfig.isCombineCategorySubjects()) {
            targets.add(Target.subject("004005006"));   // 理综
            targets.add(Target.subject("007008009"));   // 文综
        }

        int counter = 0;
        for (Target target : targets) {
            for (Range range : ranges) {
                dispatchTask(createTask(projectId, aggregationId).setRange(range).setTarget(target));
                counter++;
            }
        }
        LOG.info("最终为项目 " + projectId + " 的 score_map 统计发布了 " + counter + " 个任务");
    }
}
