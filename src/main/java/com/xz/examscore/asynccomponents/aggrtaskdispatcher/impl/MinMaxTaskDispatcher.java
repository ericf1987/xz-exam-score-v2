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

/**
 * 最高最低得分
 */
@TaskDispatcherInfo(taskType = "score_minmax", dependentTaskType = "total_score")
@Component
public class MinMaxTaskDispatcher extends TaskDispatcher {

    static final Logger LOG = LoggerFactory.getLogger(MinMaxTaskDispatcher.class);

    @Autowired
    TargetService targetService;

    @Autowired
    RangeService rangeService;

    @Override
    public void dispatch(String projectId, String aggregationId, ProjectConfig projectConfig, Map<String, List<Range>> rangesMap) {
        List<Target> targets = targetService.queryTargets(projectId, Target.PROJECT, Target.SUBJECT_COMBINATION, Target.SUBJECT);

        String[] rangeKeys = new String[]{
                Range.CLASS, Range.SCHOOL, Range.PROVINCE
        };

        List<Range> ranges = fetchRanges(rangeKeys, rangesMap);

        int counter = 0;
        for (Target target : targets) {
            for (Range range : ranges) {
                dispatchTask(createTask(projectId, aggregationId).setTarget(target).setRange(range));
                counter++;
            }
        }
        LOG.info("最终为项目 " + projectId + " 的 score_minmax 统计发布了 " + counter + " 个任务");
    }
}
