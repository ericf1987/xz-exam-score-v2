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
@TaskDispatcherInfo(taskType = "quest_deviation", dependentTaskType = "total_score")
@Component
public class QuestDeviationDispatcher extends TaskDispatcher{
    static final Logger LOG = LoggerFactory.getLogger(QuestDeviationDispatcher.class);

    @Autowired
    RangeService rangeService;

    @Autowired
    TargetService targetService;

    @Override
    public void dispatch(String projectId, String aggregationId, ProjectConfig projectConfig) {
        List<Range> ranges = rangeService.queryRanges(projectId, Range.CLASS, Range.SCHOOL);
        List<Target> targets = targetService.queryTargets(projectId, Target.QUEST);

        int counter = 0;
        for(Range range : ranges){
            for(Target target : targets){
                dispatchTask(createTask(projectId,aggregationId).setRange(range).setTarget(target));
                counter++;
                if(counter % 1000 == 0){
                    LOG.info("为项目 " + projectId + " 的 quest_deviation 统计发布了 " + counter + " 个任务");
                }
            }
        }
        LOG.info("最终为项目 " + projectId + " 的 quest_deviation 统计发布了 " + counter + " 个任务");
    }
}
