package com.xz.taskdispatchers.impl;

import com.xz.bean.ProjectConfig;
import com.xz.bean.Range;
import com.xz.services.RangeService;
import com.xz.taskdispatchers.TaskDispatcher;
import com.xz.taskdispatchers.TaskDispatcherInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 超均率
 *
 * @author yiding_he
 */
@Component
@TaskDispatcherInfo(taskType = "over_average", dependentTaskType = "average")
public class OverAverageDispatcher extends TaskDispatcher {

    @Autowired
    RangeService rangeService;

    @Override
    public void dispatch(String projectId, String aggregationId, ProjectConfig projectConfig) {
        List<Range> ranges = rangeService.queryRanges(projectId, Range.SCHOOL, Range.CLASS);

        for (Range range : ranges) {
            dispatchTask(createTask(projectId, aggregationId).setRange(range));
        }
    }
}
