package com.xz.examscore.asynccomponents.report.schools;

import com.xz.examscore.asynccomponents.report.ReportGenerator;
import com.xz.examscore.asynccomponents.report.SheetTask;
import com.xz.examscore.asynccomponents.report.classes.ReportGeneratorInfo;
import com.xz.examscore.asynccomponents.report.total.TotalCollegeEntryLevelSheets;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.TargetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author by fengye on 2016/11/4.
 */
@Component
@ReportGeneratorInfo(range= Range.SCHOOL)
public class SchoolCollegeEntryLevelReport extends ReportGenerator{
    @Autowired
    TargetService targetService;

    @Override
    protected List<SheetTask> getSheetTasks(String projectId, Range range) {
        List<SheetTask> tasks = new ArrayList<>();
        SheetTask projectTask = new SheetTask("全部科目", SchoolCollegeEntryLevelSheets.class);
        projectTask.put("target", Target.project(projectId));
        tasks.add(projectTask);
        return tasks;
    }
}
