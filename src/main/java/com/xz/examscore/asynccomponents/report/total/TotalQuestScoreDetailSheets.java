package com.xz.examscore.asynccomponents.report.total;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.server.project.ProjectQuestScoreDetailAnalysis;
import com.xz.examscore.asynccomponents.report.SheetGenerator;
import com.xz.examscore.asynccomponents.report.SheetTask;
import com.xz.examscore.asynccomponents.report.classes.ClassQuestScoreDetailSheets;
import com.xz.examscore.asynccomponents.report.schools.SchoolQuestScoreDetailSheets;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.TargetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author by fengye on 2016/7/1.
 * 总体成绩分析-基础数据-学生题目得分明细
 */
@Component
public class TotalQuestScoreDetailSheets extends SheetGenerator {

    @Autowired
    ProjectQuestScoreDetailAnalysis projectQuestScoreDetailAnalysis;

    @Autowired
    TargetService targetService;

    @Autowired
    ClassQuestScoreDetailSheets classQuestScoreDetailSheets;

    @Autowired
    SchoolQuestScoreDetailSheets schoolQuestScoreDetailSheets;

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {
        Target target = sheetTask.get("target");
        String subjectId = target.getId().toString();
        Param param = new Param().
                setParameter("projectId", projectId).
                setParameter("subjectId", subjectId);
        Result result = projectQuestScoreDetailAnalysis.execute(param);
        schoolQuestScoreDetailSheets.setupHeader(excelWriter, result);
        schoolQuestScoreDetailSheets.fillData(excelWriter, result);
    }

}
