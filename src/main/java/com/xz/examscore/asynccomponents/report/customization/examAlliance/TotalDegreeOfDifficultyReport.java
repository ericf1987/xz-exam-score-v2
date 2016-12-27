package com.xz.examscore.asynccomponents.report.customization.examAlliance;

import com.xz.examscore.asynccomponents.report.ReportGenerator;
import com.xz.examscore.asynccomponents.report.SheetTask;
import com.xz.examscore.asynccomponents.report.classes.ReportGeneratorInfo;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author by fengye on 2016/12/27.
 */
@ReportGeneratorInfo(range = Range.PROVINCE)
@Component
public class TotalDegreeOfDifficultyReport extends ReportGenerator{

    @Override
    protected List<SheetTask> getSheetTasks(String projectId, Range range) {
        List<SheetTask> tasks = new ArrayList<>();
        SheetTask projectTask = new SheetTask("各科难度系数", TotalDegreeOfDifficultySheets.class);
        projectTask.put("target", Target.project(projectId));
        tasks.add(projectTask);
        return tasks;
    }
}
