package com.xz.report.total;

import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.report.ReportGenerator;
import com.xz.report.SheetTask;
import com.xz.report.classes.ReportGeneratorInfo;
import com.xz.services.SubjectService;
import com.xz.services.TargetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author by fengye on 2016/6/24.
 * 总体/基础/基础数据统计
 */
@Component
@ReportGeneratorInfo(range = Range.PROVINCE)
public class TotalBasicDataReport  extends ReportGenerator {
    @Autowired
    TargetService targetService;

    @Override
    protected List<SheetTask> getSheetTasks(String projectId, Range range) {
        List<SheetTask> tasks = new ArrayList<>();
        SheetTask projectTask = new SheetTask("全部科目", TotalBasicDataSheets.class);
        projectTask.setTarget(Target.project(projectId));
        tasks.add(projectTask);
        return tasks;
    }
}
