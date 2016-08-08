package com.xz.report.classes;

import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.report.ReportGenerator;
import com.xz.report.SheetTask;
import com.xz.report.schools.SchoolPointAbilityLevelSheets;
import com.xz.services.SubjectService;
import com.xz.services.TargetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author by fengye on 2016/8/1.
 * 班级成绩分析-试卷分析-知识点能力层级分析
 */
@Component
public class ClassPointAbilityLevelReport extends ReportGenerator{
    @Autowired
    TargetService targetService;

    @Override
    protected List<SheetTask> getSheetTasks(String projectId, Range range) {
        List<SheetTask> tasks = new ArrayList<>();
        List<Target> subjects = targetService.queryTargets(projectId, Target.SUBJECT);
        for (Target subject : subjects) {
            String subjectName = SubjectService.getSubjectName(subject.getId().toString());
            SheetTask projectTask = new SheetTask(subjectName, ClassPointAblilityLevelSheets.class);
            projectTask.put("target", subject);
            tasks.add(projectTask);
        }
        return tasks;
    }
}
