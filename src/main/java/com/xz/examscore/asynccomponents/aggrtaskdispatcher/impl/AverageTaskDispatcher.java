package com.xz.examscore.asynccomponents.aggrtaskdispatcher.impl;

import com.mongodb.client.MongoDatabase;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMessage;
import com.xz.examscore.asynccomponents.aggrtaskdispatcher.TaskDispatcher;
import com.xz.examscore.asynccomponents.aggrtaskdispatcher.TaskDispatcherInfo;
import com.xz.examscore.bean.ProjectConfig;
import com.xz.examscore.bean.Range;
import com.xz.examscore.services.RangeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@TaskDispatcherInfo(taskType = "average", dependentTaskType = "total_score")
public class AverageTaskDispatcher extends TaskDispatcher {

    static final Logger LOG = LoggerFactory.getLogger(AverageTaskDispatcher.class);

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    RangeService rangeService;

    @Override
    public void dispatch(String projectId, String aggregationId, ProjectConfig projectConfig) {

        List<Range> ranges = rangeService.queryRanges(
                projectId, Range.CLASS, Range.SCHOOL, Range.PROVINCE);

        int counter = 0;
        for (Range range : ranges) {
            dispatchTask(createTask(projectId, aggregationId).setRange(range));
            counter++;
            if (counter % 1000 == 0) {
                LOG.info("为项目 " + projectId + " 的 average 统计发布了 " + counter + " 个任务");
            }
        }
        LOG.info("最终为项目 " + projectId + " 的 average 统计发布了 " + counter + " 个任务");
    }
}
