package com.xz.examscore.asynccomponents.report.customization.examAlliance;

import com.xz.examscore.asynccomponents.report.ReportGenerator;
import com.xz.examscore.asynccomponents.report.SheetTask;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author by fengye on 2016/12/27.
 */
@Component
public class TotalAverageDistributionReport extends ReportGenerator {

    @Override
    protected List<SheetTask> getSheetTasks(String projectId, Range range) {
        List<SheetTask> tasks = new ArrayList<>();
        SheetTask projectTask = new SheetTask("平均分分布", TotalAverageDistributionSheets.class);
        projectTask.put("target", Target.project(projectId));
        tasks.add(projectTask);
        return tasks;
    }
}
