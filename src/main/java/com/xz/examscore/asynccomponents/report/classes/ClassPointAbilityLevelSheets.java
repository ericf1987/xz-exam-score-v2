package com.xz.examscore.asynccomponents.report.classes;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.api.Param;
import com.xz.examscore.asynccomponents.report.SheetGenerator;
import com.xz.examscore.asynccomponents.report.SheetTask;
import com.xz.examscore.asynccomponents.report.biz.classes.ClassPointAbilityLevelBiz;
import com.xz.examscore.asynccomponents.report.schools.SchoolPointAbilityLevelSheets;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.ClassService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author by fengye on 2016/8/1.
 * 班级成绩分析-试卷分析-知识点能力层级分析
 */
@Component
public class ClassPointAbilityLevelSheets extends SheetGenerator {

    @Autowired
    SchoolPointAbilityLevelSheets schoolPointAbilityLevelSheets;

    @Autowired
    ClassPointAbilityLevelBiz classPointAbilityLevelBiz;

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
        Result result = classPointAbilityLevelBiz.execute(param);
        schoolPointAbilityLevelSheets.setupHeader(excelWriter, result);
        schoolPointAbilityLevelSheets.setupSecondaryHeader(excelWriter, result);
        schoolPointAbilityLevelSheets.fillDetailData(excelWriter, result);
    }
}
