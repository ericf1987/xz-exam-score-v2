package com.xz.examscore.asynccomponents.report.customization.examAlliance;

import com.xz.examscore.asynccomponents.report.ReportGenerator;
import com.xz.examscore.asynccomponents.report.SheetTask;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author by fengye on 2017/1/11.
 */
@Component
public class TotalScoreMinAboveReport extends ReportGenerator{
    @Override
    protected List<SheetTask> getSheetTasks(String projectId, Range range) {
        List<SheetTask> tasks = new ArrayList<>();
        SheetTask projectTask = new SheetTask("学校分数段累积统计（10分段）", TotalScoreMinAboveSheets.class);
        projectTask.put("target", Target.project(projectId));
        tasks.add(projectTask);
        return tasks;
    }
}
