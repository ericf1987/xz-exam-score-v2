package com.xz.examscore.asynccomponents.report.schools;

import com.xz.examscore.asynccomponents.report.ReportGenerator;
import com.xz.examscore.asynccomponents.report.SheetTask;
import com.xz.examscore.bean.Range;
import com.xz.examscore.services.TargetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author by fengye on 2016/12/20.
 */
@Component
public class SchoolCombinedRankLevelReport extends ReportGenerator{
    @Autowired
    TargetService targetService;

    @Override
    protected List<SheetTask> getSheetTasks(String projectId, Range range) {
        List<SheetTask> tasks = new ArrayList<>();

        SheetTask projectTask = new SheetTask("本次考试", SchoolCombinedRankLevelSheets.class);
        tasks.add(projectTask);

        return tasks;
    }
}
