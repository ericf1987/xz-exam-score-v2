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
 * 非学生的题型得分（平均分）统计
 */
@TaskDispatcherInfo(taskType = "nonstu_quest_type_score", dependentTaskType = "student_quest_type_score")
@Component
public class NonStuQuestTypeDispatcher extends TaskDispatcher {

    @Autowired
    RangeService rangeService;

    @Override
    public void dispatch(String projectId, String aggregationId, ProjectConfig projectConfig) {

        List<Range> rangeList = rangeService.queryRanges(
                projectId, Range.PROVINCE, Range.CITY, Range.AREA, Range.SCHOOL, Range.CLASS);

        for (Range range : rangeList) {
            dispatchTask(createTask(projectId, aggregationId).setRange(range));
        }
    }
}
