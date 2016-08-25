package com.xz.examscore.asynccomponents.report.classes;

import com.xz.examscore.asynccomponents.report.ReportGenerator;
import com.xz.examscore.asynccomponents.report.SheetTask;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.TargetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author by fengye on 2016/6/24.
 * 班级成绩分析-基础数据-学生各科成绩明细
 */
@Component
@ReportGeneratorInfo(range= Range.CLASS)
public class ClassBasicDataReport extends ReportGenerator {
    @Autowired
    TargetService targetService;

    @Override
    protected List<SheetTask> getSheetTasks(String projectId, Range range) {
        List<SheetTask> tasks = new ArrayList<>();
        SheetTask projectTask = new SheetTask("全部科目", ClassBasicDataSheets.class);
        projectTask.setTarget(Target.project(projectId));
        tasks.add(projectTask);
        return tasks;
    }
}
