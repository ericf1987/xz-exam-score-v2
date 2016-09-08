package com.xz.examscore.asynccomponents.aggrtaskdispatcher.impl;

import com.xz.examscore.asynccomponents.aggrtaskdispatcher.TaskDispatcher;
import com.xz.examscore.asynccomponents.aggrtaskdispatcher.TaskDispatcherInfo;
import com.xz.examscore.bean.ProjectConfig;
import com.xz.examscore.bean.Range;
import com.xz.examscore.services.RangeService;
import com.xz.examscore.services.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 科目贡献率
 */
@Component
@TaskDispatcherInfo(taskType = "subject_rate", dependentTaskType = "average")
public class SubjectRateDispatcher extends TaskDispatcher {

    @Autowired
    RangeService rangeService;

    @Autowired
    StudentService studentService;

    @Override
    public void dispatch(String projectId, String aggregationId, ProjectConfig projectConfig) {

        List<Range> ranges = rangeService.queryRanges(
                projectId, Range.PROVINCE, Range.SCHOOL, Range.CLASS);

        for (Range range : ranges) {
            dispatchTask(createTask(projectId, aggregationId).setRange(range));
        }

        // 为每个学生发布任务需要调用单独的方法，否则可能导致效率低下
        dispatchTaskForEveryStudent(projectId, aggregationId);
    }
}
