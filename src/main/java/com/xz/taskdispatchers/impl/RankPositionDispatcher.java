package com.xz.taskdispatchers.impl;

import com.xz.bean.ProjectConfig;
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

/**
 * @author by fengye on 2016/5/27.
 */
@TaskDispatcherInfo(taskType = "rank_position", dependentTaskType = "score_map")
@Component
public class RankPositionDispatcher extends TaskDispatcher {
    static final Logger LOG = LoggerFactory.getLogger(RankPositionDispatcher.class);

    @Autowired
    RangeService rangeService;

    @Autowired
    TargetService targetService;

    @Override
    public void dispatch(String projectId, String aggregationId, ProjectConfig projectConfig) {
        List<Range> ranges = rangeService.queryRanges(projectId, Range.CLASS, Range.SCHOOL, Range.PROVINCE);
        List<Target> targets = targetService.queryTargets(projectId, Target.QUEST, Target.SUBJECT, Target.PROJECT);

        int counter = 0;
        for (Range range : ranges) {
            for (Target target : targets) {
                dispatchTask(createTask(projectId, aggregationId).setRange(range).setTarget(target));
                counter++;
                if (counter % 1000 == 0) {
                    LOG.info("为项目 " + projectId + " 的 rank_position 统计发布了 " + counter + " 个任务");
                }
            }
        }
        LOG.info("最终为项目 " + projectId + " 的 rank_position 统计发布了 " + counter + " 个任务");
    }
}
