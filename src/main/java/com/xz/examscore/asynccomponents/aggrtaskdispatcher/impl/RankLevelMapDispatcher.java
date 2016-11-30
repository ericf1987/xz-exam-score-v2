package com.xz.examscore.asynccomponents.aggrtaskdispatcher.impl;

import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMessage;
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
 * 等第排名统计
 *
 * @author fengye
 */
@TaskDispatcherInfo(taskType = "rank_level_map", dependentTaskType = "rank_level")
@Component
public class RankLevelMapDispatcher extends TaskDispatcher {

    static final Logger LOG = LoggerFactory.getLogger(RankLevelMapDispatcher.class);

    @Autowired
    RangeService rangeService;

    @Autowired
    TargetService targetService;

    @Override
    public void dispatch(String projectId, String aggregationId, ProjectConfig projectConfig, Map<String, List<Range>> rangesMap) {
        String[] rangeKeys = new String[]{
                Range.CLASS, Range.SCHOOL, Range.PROVINCE
        };

        List<Range> ranges = fetchRanges(rangeKeys, rangesMap);
        List<Target> targets = targetService.queryTargets(projectId, Target.PROJECT, Target.SUBJECT_COMBINATION, Target.SUBJECT);

        int counter = 0;
        for (Range range : ranges) {
            for (Target target : targets) {
                AggrTaskMessage task = createTask(projectId, aggregationId).setRange(range).setTarget(target);
                dispatchTask(task);
                counter++;
            }
        }
        LOG.info("最终为项目 " + projectId + " 的 rank_level_map 统计发布了 " + counter + " 个任务");
    }
}
