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
 * @author by fengye on 2016/6/24.
 * 总体成绩分析-基础分析-排名统计
 */
@Component
@ReportGeneratorInfo(range = Range.PROVINCE)
public class TotalBasicRankReport extends ReportGenerator {

    @Autowired
    TargetService targetService;

    @Override
    protected List<SheetTask> getSheetTasks(String projectId, Range range) {
        List<SheetTask> tasks = new ArrayList<>();

        SheetTask projectTask = new SheetTask("全部科目", TotalBasicRankSheet.class);
        projectTask.setTarget(Target.project(projectId));
        tasks.add(projectTask);

        List<Target> subjects = targetService.queryTargets(projectId, Target.SUBJECT);
        for (Target subject : subjects) {
            String subjectName = SubjectService.getSubjectName(subject.getId().toString());
            SheetTask subjectTask = new SheetTask(subjectName, TotalBasicRankSheet.class);
            subjectTask.setTarget(subject);
            tasks.add(subjectTask);
        }
        return tasks;
    }
}
