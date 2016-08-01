package com.xz.report.classes;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.api.server.school.SchoolPointAbilityLevelAnalysis;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.report.SheetGenerator;
import com.xz.report.SheetTask;
import com.xz.report.schools.SchoolPointAbilityLevelSheets;
import com.xz.services.ClassService;
import com.xz.services.SchoolService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author by fengye on 2016/8/1.
 */
@Component
public class ClassPointAblilityLevelSheets extends SheetGenerator {

    @Autowired
    SchoolPointAbilityLevelAnalysis schoolPointAbilityLevelAnalysis;

    @Autowired
    SchoolPointAbilityLevelSheets schoolPointAbilityLevelSheets;

    @Autowired
    ClassService classService;

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {
        Target target = sheetTask.get("target");
        String subjectId = target.getId().toString();
        String classId = sheetTask.getRange().getId();

        Document classDoc = classService.findClass(projectId, classId);
        String schoolId = classDoc.getString("schoolId");

        Param param = new Param().setParameter("projectId", projectId)
                .setParameter("subjectId", subjectId)
                .setParameter("schoolId", schoolId)
                .setParameter("classId", classId);
        Result result = schoolPointAbilityLevelAnalysis.execute(param);
        schoolPointAbilityLevelSheets.setupHeader(excelWriter, result);
        schoolPointAbilityLevelSheets.setupSecondaryHeader(excelWriter, result);
        schoolPointAbilityLevelSheets.fillDetailData(excelWriter, result);
    }
}
