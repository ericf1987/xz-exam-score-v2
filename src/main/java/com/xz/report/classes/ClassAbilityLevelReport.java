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
 * @author by fengye on 2016/6/30.
 */
@Component
@ReportGeneratorInfo(range= Range.CLASS)
public class ClassAbilityLevelReport extends ReportGenerator{
    @Autowired
    TargetService targetService;

    @Override
    protected List<SheetTask> getSheetTasks(String projectId, Range range) {
        List<SheetTask> taskList = new ArrayList<>();
        List<Target> subjectIds = targetService.queryTargets(projectId, Target.SUBJECT);
        for(Target subject : subjectIds){
            String subjectName = SubjectService.getSubjectName(subject.getId().toString());
            SheetTask projectTask = new SheetTask(subjectName, ClassAbilityLevelSheet.class);
            projectTask.put("target", subject);
            taskList.add(projectTask);
        }
        return taskList;
    }
}
