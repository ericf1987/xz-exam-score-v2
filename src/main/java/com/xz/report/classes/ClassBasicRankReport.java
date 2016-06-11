package com.xz.report.classes;

import com.xz.bean.Range;
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
 * @author by fengye on 2016/6/8.
 */
@Component
@ReportGeneratorInfo(range= Range.CLASS)
public class ClassBasicRankReport extends ReportGenerator{
    @Autowired
    TargetService targetService;

    @Autowired
    SubjectService subjectService;

    @Override
    protected List<SheetTask> getSheetTasks(String projectId, Range range) {
        List<SheetTask> tasks = new ArrayList<>();

        SheetTask projectTask = new SheetTask("成绩分析", ClassBasicRankSheet.class);
        projectTask.put("target", Target.project(projectId));
        tasks.add(projectTask);
        return tasks;
    }
}
