package com.xz.examscore.asynccomponents.report.total;

import com.xz.examscore.asynccomponents.report.ReportGenerator;
import com.xz.examscore.asynccomponents.report.SheetTask;
import com.xz.examscore.asynccomponents.report.classes.ReportGeneratorInfo;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.SubjectService;
import com.xz.examscore.services.TargetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author by fengye on 2016/6/19.
 * 总体成绩分析-尖子生情况-试卷情况分析
 */
@Component
@ReportGeneratorInfo(range = Range.PROVINCE)
public class TotalTopStudentQuestTypeReport extends ReportGenerator{

    @Autowired
    TargetService targetService;

    @Override
    protected List<SheetTask> getSheetTasks(String projectId, Range range) {
        List<SheetTask> tasks = new ArrayList<>();
        List<Target> subjects = targetService.queryTargets(projectId, Target.SUBJECT);
        for (Target subject : subjects) {
            String subjectName = SubjectService.getSubjectName(subject.getId().toString());
            SheetTask projectTask = new SheetTask(subjectName, TotalTopStudentQuestTypeSheets.class);
            projectTask.put("target", subject);
            tasks.add(projectTask);
        }
        return tasks;
    }
}
