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
 * 标准差
 *
 * @author yiding_he
 */
@TaskDispatcherInfo(taskType = "std_deviation", dependentTaskType = "average")
@Component
public class StdDeviationDispatcher extends TaskDispatcher {
    static final Logger LOG = LoggerFactory.getLogger(StdDeviationDispatcher.class);

    @Autowired
    RangeService rangeService;

    @Autowired
    TargetService targetService;

    @Override
    public void dispatch(String projectId, String aggregationId, ProjectConfig projectConfig, Map<String, List<Range>> rangesMap) {
        String[] rangeKeys = new String[]{
                Range.PROVINCE, Range.SCHOOL, Range.CLASS
        };

        List<Range> ranges = fetchRanges(rangeKeys, rangesMap);
        List<Target> targets = targetService.queryTargets(projectId, Target.PROJECT, Target.SUBJECT);

        int counter = 0;
        for (Target target : targets) {
            for (Range range : ranges) {
                dispatchTask(createTask(projectId, aggregationId).setRange(range).setTarget(target));
                counter++;
            }
        }
        LOG.info("最终为项目 " + projectId + " 的 std_deviation 统计发布了 " + counter + " 个任务");
    }
}
