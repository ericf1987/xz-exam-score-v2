package com.xz.report.total;

import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.report.ReportGenerator;
import com.xz.report.SheetTask;
import com.xz.report.classes.ReportGeneratorInfo;
import com.xz.services.SubjectService;
import com.xz.services.TargetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 总体/试卷/客观题分析
 */
@Component
@ReportGeneratorInfo(range = Range.PROVINCE)
public class TotalPaperObjectiveReport extends ReportGenerator{

        @Autowired
        TargetService targetService;

        @Override
        protected List<SheetTask> getSheetTasks(String projectId, Range range) {
            List<SheetTask> tasks = new ArrayList<>();

            SheetTask projectTask = new SheetTask("全部科目", TotalPaperObjectiveSheets.class);
            projectTask.put("target", Target.project(projectId));
            tasks.add(projectTask);

            List<Target> subjects = targetService.queryTargets(projectId, Target.SUBJECT);
            for (Target subject : subjects) {
                String subjectName = SubjectService.getSubjectName(subject.getId().toString());
                projectTask = new SheetTask(subjectName, TotalPaperObjectiveSheets.class);
                projectTask.put("target", subject);
                tasks.add(projectTask);
            }

            return tasks;
        }
}
