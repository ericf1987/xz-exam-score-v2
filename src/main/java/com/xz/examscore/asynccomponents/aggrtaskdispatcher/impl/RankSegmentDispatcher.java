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
 * @author by fengye on 2016/5/27.
 */
@TaskDispatcherInfo(taskType = "rank_segment", dependentTaskType = "rank_level")
@Component
public class RankSegmentDispatcher extends TaskDispatcher{
    static final Logger LOG = LoggerFactory.getLogger(RankSegmentDispatcher.class);

    @Autowired
    RangeService rangeService;

    @Autowired
    TargetService targetService;

    @Override
    public void dispatch(String projectId, String aggregationId, ProjectConfig projectConfig, Map<String, List<Range>> rangesMap) {
        String[] rangeKeys = new String[]{
                Range.SCHOOL, Range.PROVINCE
        };

        List<Range> ranges = fetchRanges(rangeKeys, rangesMap);
        List<Target> targets = targetService.queryTargets(projectId, Target.QUEST, Target.SUBJECT, Target.PROJECT);

        int counter = 0;
        for(Range range : ranges){
            for(Target target : targets){
                dispatchTask(createTask(projectId,aggregationId).setRange(range).setTarget(target));
                counter++;
                if(counter % 1000 == 0){
                    LOG.info("为项目 " + projectId + " 的 rank_segment 统计发布了 " + counter + " 个任务");
                }
            }
        }
        LOG.info("最终为项目 " + projectId + " 的 rank_segment 统计发布了 " + counter + " 个任务");
    }
}
