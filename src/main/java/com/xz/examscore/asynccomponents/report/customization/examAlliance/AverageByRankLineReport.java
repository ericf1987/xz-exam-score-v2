package com.xz.examscore.asynccomponents.report.customization.examAlliance;

import com.xz.examscore.asynccomponents.report.ReportGenerator;
import com.xz.examscore.asynccomponents.report.SheetTask;
import com.xz.examscore.asynccomponents.report.classes.ReportGeneratorInfo;
import com.xz.examscore.bean.Range;
import com.xz.examscore.util.DoubleUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author by fengye on 2016/12/5.
 */
@ReportGeneratorInfo(range = Range.CLASS)
@Component
public class AverageByRankLineReport extends ReportGenerator {

    @Override
    protected List<SheetTask> getSheetTasks(String projectId, Range range) {
        List<SheetTask> tasks = new ArrayList<>();
        double rate = 0.1;
        for (int i = 1; i <= 9; i++) {
            String rankSegment = String.valueOf(rate * i);
            SheetTask projectTask = new SheetTask(DoubleUtils.toPercent(rate * i), AverageByRankLineSheets.class);
            projectTask.put("rankSegment", rankSegment);
            tasks.add(projectTask);
        }
        return tasks;
    }
}
