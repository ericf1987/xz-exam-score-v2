package com.xz.examscore.asynccomponents.report.total;

import com.xz.examscore.asynccomponents.report.ReportGenerator;
import com.xz.examscore.asynccomponents.report.SheetTask;
import com.xz.examscore.asynccomponents.report.classes.ReportGeneratorInfo;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.SubjectService;
import com.xz.examscore.services.TargetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author by fengye on 2016/7/1.
 * 总体成绩分析-基础数据-学生题目得分明细
 */
@Component
@ReportGeneratorInfo(range= Range.PROVINCE)
public class TotalQuestScoreDetailReport extends ReportGenerator{
    @Autowired
    TargetService targetService;
    @Override
    protected List<SheetTask> getSheetTasks(String projectId, Range range) {
        List<SheetTask> sheetTasks = new ArrayList<>();
        List<Target> subjects = targetService.queryTargets(projectId, Target.SUBJECT);
        for(Target subject : subjects){
            String subjectName = SubjectService.getSubjectName(subject.getId().toString());
            SheetTask subjectTask = new SheetTask(subjectName, TotalQuestScoreDetailSheets.class);
            subjectTask.put("target", subject);
            sheetTasks.add(subjectTask);
        }
        return sheetTasks;
    }
}
