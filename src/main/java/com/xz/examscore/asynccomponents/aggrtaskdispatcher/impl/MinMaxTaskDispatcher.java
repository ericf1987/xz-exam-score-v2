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

@TaskDispatcherInfo(taskType = "score_minmax", dependentTaskType = "total_score")
@Component
public class MinMaxTaskDispatcher extends TaskDispatcher {

    static final Logger LOG = LoggerFactory.getLogger(MinMaxTaskDispatcher.class);

    @Autowired
    TargetService targetService;

    @Autowired
    RangeService rangeService;

    @Override
    public void dispatch(String projectId, String aggregationId, ProjectConfig projectConfig) {

        List<Target> targets = targetService.queryTargets(projectId, Target.PROJECT, Target.SUBJECT);
        List<Range> ranges = rangeService.queryRanges(projectId, Range.CLASS, Range.SCHOOL, Range.PROVINCE);

        int counter = 0;
        for (Target target : targets) {
            for (Range range : ranges) {
                dispatchTask(createTask(projectId, aggregationId).setTarget(target).setRange(range));
                counter++;
                if (counter % 1000 == 0) {
                    LOG.info("为项目 " + projectId + " 的 score_minmax 统计发布了 " + counter + " 个任务");
                }
            }
        }
        LOG.info("最终为项目 " + projectId + " 的 score_minmax 统计发布了 " + counter + " 个任务");
    }
}
