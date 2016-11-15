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

/**
 * 四率统计
 *
 * @author yiding_he
 */
@Component
@TaskDispatcherInfo(taskType = "score_level_map", dependentTaskType = "score_rate")
public class ScoreLevelMapDispatcher extends TaskDispatcher {
    static final Logger LOG = LoggerFactory.getLogger(ScoreLevelMapDispatcher.class);

    @Autowired
    RangeService rangeService;

    @Autowired
    TargetService targetService;

    @Override
    public void dispatch(String projectId, String aggregationId, ProjectConfig projectConfig) {

        List<Range> ranges = rangeService.queryRanges(
                projectId, Range.PROVINCE, Range.SCHOOL, Range.CLASS);

        List<Target> targets = targetService.queryTargets(projectId, Target.SUBJECT, Target.PROJECT);

        int counter = 0;
        for (Target target : targets) {
            for (Range range : ranges) {
                dispatchTask(createTask(projectId, aggregationId).setRange(range).setTarget(target));
                counter++;
                if(counter % 1000 == 0){
                    LOG.info("为项目 " + projectId + " 的 score_level_map 统计发布了 " + counter + " 个任务");
                }
            }
        }
        LOG.info("最终为项目 " + projectId + " 的 score_level_map 统计发布了 " + counter + " 个任务");
    }
}
