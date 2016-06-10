package com.xz.report.classes;

import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.report.ReportGenerator;
import com.xz.report.SheetTask;
import com.xz.report.schools.SchoolBasicRankSheet;
import com.xz.report.total.TotalBasicScoreReport;
import com.xz.services.SubjectService;
import com.xz.services.TargetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * (description)
 * created at 16/05/31
 *
 * @author yiding_he
 */
@Component
@ReportGeneratorInfo(range= Range.CLASS)
public class ClassBasicScoreReport extends ReportGenerator {
    @Autowired
    TargetService targetService;

    @Autowired
    SubjectService subjectService;

    @Override
    protected List<SheetTask> getSheetTasks(String projectId, Range range) {
        List<SheetTask> tasks = new ArrayList<>();

        SheetTask projectTask = new SheetTask("全部科目", ClassBasicScoreSheet.class);
        projectTask.put("target", Target.project(projectId));
        tasks.add(projectTask);

        List<Target> subjects = targetService.queryTargets(projectId, Target.SUBJECT);
        for (Target subject : subjects) {
            String subjectName = SubjectService.getSubjectName(subject.getId().toString());
            projectTask = new SheetTask(subjectName, ClassBasicScoreSheet.class);
            projectTask.put("target", subject);
            tasks.add(projectTask);
        }

        return tasks;
    }
}
