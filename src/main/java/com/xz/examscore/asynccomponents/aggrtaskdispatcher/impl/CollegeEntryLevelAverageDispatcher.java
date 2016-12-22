package com.xz.examscore.asynccomponents.aggrtaskdispatcher.impl;

import com.mongodb.client.MongoDatabase;
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
 * @author by fengye on 2016/10/24.
 *         本科上线学生的平均分
 */
@Component
@TaskDispatcherInfo(taskType = "college_entry_level_average", dependentTaskType = "college_entry_level")
public class CollegeEntryLevelAverageDispatcher extends TaskDispatcher {

    static final Logger LOG = LoggerFactory.getLogger(CollegeEntryLevelAverageDispatcher.class);

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    RangeService rangeService;

    @Autowired
    TargetService targetService;

    @Override
    public void dispatch(String projectId, String aggregationId, ProjectConfig projectConfig, Map<String, List<Range>> rangesMap) {

        //统计本科批次只统计联考维度
        String[] rangeKeys = new String[]{
                Range.PROVINCE
        };

        List<Range> ranges = fetchRanges(rangeKeys, rangesMap);

        List<Target> targets = targetService.queryTargets(projectId, Target.PROJECT, Target.SUBJECT, Target.SUBJECT_COMBINATION);

        int counter = 0;
        for (Range range : ranges) {
            for (Target target : targets) {
                dispatchTask(createTask(projectId, aggregationId).setRange(range).setTarget(target));
                counter++;
            }
        }
        LOG.info("最终为项目 " + projectId + " 的 college_entry_level_average 统计发布了 " + counter + " 个任务");
    }
}
