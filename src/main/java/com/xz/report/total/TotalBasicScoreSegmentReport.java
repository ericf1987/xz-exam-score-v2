package com.xz.report.total;

import com.xz.bean.Target;
import com.xz.report.ReportGenerator;
import com.xz.report.SheetTask;
import com.xz.services.SubjectService;
import com.xz.services.TargetService;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 总体/基础/分数段统计
 */
@Component
public class TotalBasicScoreSegmentReport extends ReportGenerator {
    @Autowired
    TargetService targetService;

    @Autowired
    SubjectService subjectService;

    @Override
    protected List<SheetTask> getSheetTasks(String projectId) {
        List<SheetTask> tasks = new ArrayList<SheetTask>();

        SheetTask projectTask = new SheetTask("全部科目", TotalBasicScoreSegmentSheet.class);
        projectTask.put("target", Target.project(projectId));
        tasks.add(projectTask);

        List<Target> subjects = targetService.queryTargets(projectId, Target.SUBJECT);
        for (Target subject : subjects) {
            String subjectName = subjectService.getSubjectName(subject.getId().toString());
            projectTask = new SheetTask(subjectName, TotalBasicScoreSegmentSheet.class);
            projectTask.put("target", subject);
            tasks.add(projectTask);
        }
        return tasks;
    }
}
