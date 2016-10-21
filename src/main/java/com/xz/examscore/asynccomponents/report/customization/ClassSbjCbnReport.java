package com.xz.examscore.asynccomponents.report.customization;

import com.xz.examscore.asynccomponents.report.ReportGenerator;
import com.xz.examscore.asynccomponents.report.SheetTask;
import com.xz.examscore.asynccomponents.report.classes.ReportGeneratorInfo;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.SubjectCombinationService;
import com.xz.examscore.services.TargetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author by fengye on 2016/10/20.
 */
@ReportGeneratorInfo(range = Range.SCHOOL)
@Component
public class ClassSbjCbnReport extends ReportGenerator{
    @Autowired
    TargetService targetService;
    @Override
    protected List<SheetTask> getSheetTasks(String projectId, Range range) {
        List<SheetTask> tasks = new ArrayList<>();

        List<Target> subjectCombinations = targetService.queryTargets(projectId, Target.SUBJECT_COMBINATION);
        for (Target subjectCombination : subjectCombinations) {
            String subjectCombinationName = SubjectCombinationService.getSubjectCombinationName(subjectCombination.getId().toString());
            SheetTask projectTask = new SheetTask(subjectCombinationName, ClassSbjCbnSheets.class);
            projectTask.put("target", subjectCombination);
            tasks.add(projectTask);
        }

        return tasks;
    }
}
