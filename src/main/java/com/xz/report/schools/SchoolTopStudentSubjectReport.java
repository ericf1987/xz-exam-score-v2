package com.xz.report.schools;

import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.report.ReportGenerator;
import com.xz.report.SheetTask;
import com.xz.report.classes.ReportGeneratorInfo;
import com.xz.report.total.TotalPaperQuestTypeSheets;
import com.xz.services.SubjectService;
import com.xz.services.TargetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author by fengye on 2016/6/19.
 */
@Component
@ReportGeneratorInfo(range= Range.SCHOOL)
public class SchoolTopStudentSubjectReport extends ReportGenerator{
    @Autowired
    TargetService targetService;

    @Override
    protected List<SheetTask> getSheetTasks(String projectId, Range range) {
        List<SheetTask> tasks = new ArrayList<>();

        SheetTask projectTask = new SheetTask("全部科目", SchoolTopStudentSubjectSheets.class);
        projectTask.put("target", Target.project(projectId));
        tasks.add(projectTask);

        List<Target> subjects = targetService.queryTargets(projectId, Target.SUBJECT);
        for (Target subject : subjects) {
            String subjectName = SubjectService.getSubjectName(subject.getId().toString());
            projectTask = new SheetTask(subjectName, SchoolTopStudentSubjectSheets.class);
            projectTask.put("target", subject);
            tasks.add(projectTask);
        }

        return tasks;
    }
}
