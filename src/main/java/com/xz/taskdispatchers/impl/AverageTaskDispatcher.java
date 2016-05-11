package com.xz.taskdispatchers.impl;

import com.mongodb.client.MongoDatabase;
import com.xz.bean.Range;
import com.xz.services.RangeService;
import com.xz.taskdispatchers.TaskDispatcher;
import com.xz.taskdispatchers.TaskDispatcherInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@TaskDispatcherInfo(taskType = "average")
public class AverageTaskDispatcher extends TaskDispatcher {

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    RangeService rangeService;

    @Override
    public void dispatch(String projectId) {

        List<Range> ranges = rangeService.queryRanges(
                projectId, Range.CLASS, Range.SCHOOL, Range.AREA, Range.CITY, Range.PROVINCE);

        for (Range range : ranges) {
            dispatchTask(createTask(projectId).setRange(range));
        }
    }
}
