package com.xz.report.schools;

import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.report.ReportGenerator;
import com.xz.report.SheetTask;
import com.xz.services.TargetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author by fengye on 2016/7/5.
 * 学校成绩分析-试卷分析-知识点分析
 */
@Component
public class SchoolPointReport extends ReportGenerator{
    @Autowired
    TargetService targetService;

    @Override
    protected List<SheetTask> getSheetTasks(String projectId, Range range) {
        List<SheetTask> sheetTasks = new ArrayList<>();
        List<Target> subjects = targetService.queryTargets(projectId, Target.SUBJECT);
        for(Target subject : subjects){
            String subjectName = subject.getName();
            SheetTask sheetTask = new SheetTask(subjectName, SchoolPointSheets.class);
            sheetTask.put("target", subject);
            sheetTasks.add(sheetTask);
        }
        return sheetTasks;
    }
}
