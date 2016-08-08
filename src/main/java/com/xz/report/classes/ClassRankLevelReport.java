package com.xz.report.classes;

import com.xz.bean.Range;
import com.xz.report.ReportGenerator;
import com.xz.report.SheetTask;
import com.xz.services.TargetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author by fengye on 2016/7/21.
 * 班级成绩分析-基础分析-等第统计
 */
@Component
public class ClassRankLevelReport extends ReportGenerator{
    @Autowired
    TargetService targetService;

    @Override
    protected List<SheetTask> getSheetTasks(String projectId, Range range) {
        List<SheetTask> tasks = new ArrayList<>();

        SheetTask projectTask = new SheetTask("本次考试", ClassRankLevelSheets.class);
        tasks.add(projectTask);

        return tasks;
    }
}
