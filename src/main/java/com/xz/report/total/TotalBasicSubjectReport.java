package com.xz.report.total;

import com.xz.bean.Target;
import com.xz.report.ReportGenerator;
import com.xz.report.SheetTask;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 总体/基础/学科分析
 */
@Component
public class TotalBasicSubjectReport extends ReportGenerator{
    @Override
    protected List<SheetTask> getSheetTasks(String projectId) {
        List<SheetTask> tasks = new ArrayList<>();

        SheetTask projectTask = new SheetTask("全部科目", TotalBasicSubjectSheet.class);
        projectTask.put("target", Target.project(projectId));
        tasks.add(projectTask);
        return tasks;
    }
}
