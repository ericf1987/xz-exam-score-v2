package com.xz.examscore.asynccomponents.report.total;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.examscore.api.server.project.ProjectPointAbilityLevelAnalysis;
import com.xz.examscore.asynccomponents.report.SheetGenerator;
import com.xz.examscore.asynccomponents.report.SheetTask;
import com.xz.examscore.asynccomponents.report.schools.SchoolPointAbilityLevelSheets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author by fengye on 2016/6/30.
 * 总体成绩分析-试卷分析-双向细目分析
 */
@Component
public class TotalPointAbilityLevelSheet extends SheetGenerator {
    @Autowired
    ProjectPointAbilityLevelAnalysis projectPointAbilityLevelAnalysis;

    @Autowired
    SchoolPointAbilityLevelSheets schoolPointAbilityLevelSheets;

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {
/*        Target target = sheetTask.get("target");
        String subjectId = target.getId().toString();
        Range schoolRange = sheetTask.getRange();
        Param param = new Param().setParameter("projectId", projectId)
                .setParameter("subjectId", subjectId)
                .setParameter("schoolId", schoolRange.getId());
        Result result = projectPointAbilityLevelAnalysis.execute(param);
        schoolPointAbilityLevelSheets.setupHeader(excelWriter, result);
        schoolPointAbilityLevelSheets.setupSecondaryHeader(excelWriter, result);
        schoolPointAbilityLevelSheets.fillDetailData(excelWriter, result);*/
    }
}
