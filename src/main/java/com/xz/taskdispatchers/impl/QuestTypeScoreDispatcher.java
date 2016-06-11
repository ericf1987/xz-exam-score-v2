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
 * 统计单个学生题型得分
 */
@Component
@TaskDispatcherInfo(taskType = "quest_type_score")
public class QuestTypeScoreDispatcher extends TaskDispatcher {

    @Autowired
    RangeService rangeService;

    @Override
    public void dispatch(String projectId, String aggregationId, ProjectConfig projectConfig) {
        List<Range> students = rangeService.queryRanges(projectId, Range.STUDENT);
        for (Range student : students) {
            dispatchTask(createTask(projectId, aggregationId).setRange(student));
        }
    }
}
