package com.xz.examscore.asynccomponents.aggrtaskdispatcher.impl;

import com.xz.examscore.asynccomponents.aggrtaskdispatcher.TaskDispatcher;
import com.xz.examscore.asynccomponents.aggrtaskdispatcher.TaskDispatcherInfo;
import com.xz.examscore.bean.ProjectConfig;
import com.xz.examscore.bean.Range;
import com.xz.examscore.services.RangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 非学生的题型得分（平均分）统计
 */
@TaskDispatcherInfo(taskType = "quest_type_score_average", dependentTaskType = "quest_type_score", isAdvanced = true)
@Component
public class QuestTypeScoreAverageDispatcher extends TaskDispatcher {

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
