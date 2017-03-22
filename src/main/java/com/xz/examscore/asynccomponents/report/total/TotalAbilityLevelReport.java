package com.xz.examscore.asynccomponents.report.total;

import com.xz.examscore.asynccomponents.report.ReportGenerator;
import com.xz.examscore.asynccomponents.report.SheetTask;
import com.xz.examscore.asynccomponents.report.schools.SchoolAbilityLevelSheets;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.SubjectService;
import com.xz.examscore.services.TargetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author by fengye on 2017/3/20.
 */
@Component
public class TotalAbilityLevelReport extends ReportGenerator{

    @Autowired
    TargetService targetService;

    @Override
    protected List<SheetTask> getSheetTasks(String projectId, Range range) {
        List<SheetTask> sheetTasks = new ArrayList<>();
        List<Target> subjects = targetService.queryTargets(projectId, Target.SUBJECT);
        for(Target subject : subjects){
            String subjectName = SubjectService.getSubjectName(subject.getId().toString());
            SheetTask sheetTask = new SheetTask(subjectName, TotalAbilityLevelSheets.class);
            sheetTask.put("target", subject);
            sheetTasks.add(sheetTask);
        }
        return sheetTasks;    }
}
