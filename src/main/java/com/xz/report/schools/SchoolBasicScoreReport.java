package com.xz.report.schools;

import com.xz.bean.Target;
import com.xz.report.SheetTask;
import com.xz.report.total.TotalBasicScoreReport;
import com.xz.report.total.TotalBasicScoreSheet;
import com.xz.services.SubjectService;
import com.xz.services.TargetService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * (description)
 * created at 16/05/31
 *
 * @author yiding_he
 */
public class SchoolBasicScoreReport extends TotalBasicScoreReport {
    @Autowired
    TargetService targetService;

    @Autowired
    SubjectService subjectService;

    @Override
    protected List<SheetTask> getSheetTasks(String projectId) {
        //统计学校班级所有学科分数分析
        List<SheetTask> tasks = new ArrayList<SheetTask>();
        SheetTask projectTask = new SheetTask("全部科目", SchoolBasicScoreSheet.class);
        projectTask.put("target", Target.project(projectId));
        tasks.add(projectTask);

        //查询当前考试下的所有科目
        List<Target> subjects = targetService.queryTargets(projectId, Target.SUBJECT);
        for(Target subject : subjects){
            String subjectName = subjectService.getSubjectName(subject.getId().toString());
            //每个科目进行一次处理
            projectTask.put(subjectName, SchoolBasicScoreSheet.class);
            projectTask.put("target", subject);
            tasks.add(projectTask);
        }

        return tasks;
    }
}
