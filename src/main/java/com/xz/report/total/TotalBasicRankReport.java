package com.xz.report.total;

import com.xz.bean.Target;
import com.xz.report.ReportGenerator;
import com.xz.report.SheetTask;
import com.xz.services.SubjectService;
import com.xz.services.TargetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 总体/基础/排名统计
 */
@Component
public class TotalBasicRankReport extends ReportGenerator {
    @Autowired
    TargetService targetService;

    @Autowired
    SubjectService subjectService;

    @Override
    protected List<SheetTask> getSheetTasks(String projectId) {
        List<SheetTask> tasks = new ArrayList<SheetTask>();

        SheetTask projectTask = new SheetTask("全部科目", TotalBasicRankSheet.class);
        projectTask.put("target", Target.project(projectId));
        tasks.add(projectTask);

        List<Target> subjects = targetService.queryTargets(projectId, Target.SUBJECT);
        for (Target subject : subjects) {
            String subjectName = SubjectService.getSubjectName(subject.getId().toString());
            projectTask = new SheetTask(subjectName, TotalBasicRankSheet.class);
            projectTask.put("target", subject);
            tasks.add(projectTask);
        }
        System.out.println("任务列表-->" + tasks.toString());
        return tasks;
    }
}
