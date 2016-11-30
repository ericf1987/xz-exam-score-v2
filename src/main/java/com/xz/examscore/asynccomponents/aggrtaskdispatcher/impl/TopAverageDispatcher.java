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
 * 尖子生情况统计
 */
@TaskDispatcherInfo(taskType = "top_average", dependentTaskType = "score_map")
@Component
public class TopAverageDispatcher extends TaskDispatcher {

    static final Logger LOG = LoggerFactory.getLogger(TopAverageDispatcher.class);

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
        List<Target> targets = targetService.queryTargets(projectId, Target.QUEST, Target.SUBJECT, Target.PROJECT);

        int counter = 0;
        //统计班级和学校的科目和考试
        for (Range range : ranges) {
            for (Target target : targets) {
                dispatchTask(createTask(projectId, aggregationId).setRange(range).setTarget(target));
                counter++;
            }
        }
        LOG.info("最终为项目 " + projectId + " 的 top_average 统计发布了 " + counter + " 个任务");
    }
}
