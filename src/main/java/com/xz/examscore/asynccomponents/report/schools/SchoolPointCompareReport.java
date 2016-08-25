package com.xz.examscore.asynccomponents.report.schools;

import com.xz.examscore.asynccomponents.report.ReportGenerator;
import com.xz.examscore.asynccomponents.report.SheetTask;
import com.xz.examscore.asynccomponents.report.classes.ReportGeneratorInfo;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.TargetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author by fengye on 2016/7/1.
 */
@Component
@ReportGeneratorInfo(range= Range.SCHOOL)
public class SchoolPointCompareReport extends ReportGenerator{
    @Autowired
    TargetService targetService;

    @Override
    protected List<SheetTask> getSheetTasks(String projectId, Range range) {
        List<SheetTask> sheetTasks = new ArrayList<>();
        List<Target> points = targetService.queryTargets(projectId, Target.POINT);
        for(Target point : points){
            String subjectName = point.getName();
            SheetTask sheetTask = new SheetTask(subjectName, SchoolPointCompareSheets.class);
            sheetTask.put("target", point);
            sheetTasks.add(sheetTask);
        }
        return sheetTasks;
    }
}
