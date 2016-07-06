package com.xz.report.total;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.api.server.project.ProjectQuestScoreDetailAnalysis;
import com.xz.bean.Target;
import com.xz.report.SheetGenerator;
import com.xz.report.SheetTask;
import com.xz.report.classes.ClassQuestScoreDetailSheets;
import com.xz.report.schools.SchoolQuestScoreDetailSheets;
import com.xz.services.TargetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author by fengye on 2016/7/1.
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
