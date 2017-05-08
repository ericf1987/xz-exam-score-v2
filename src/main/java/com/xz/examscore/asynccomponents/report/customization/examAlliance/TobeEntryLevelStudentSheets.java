package com.xz.examscore.asynccomponents.report.customization.examAlliance;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.ajia.Param;
import com.xz.examscore.api.server.customization.examAlliance.ToBeEntryLevelStudentAnalysis;
import com.xz.examscore.asynccomponents.report.SheetGenerator;
import com.xz.examscore.asynccomponents.report.SheetTask;
import com.xz.examscore.services.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author by fengye on 2016/12/28.
 */
@Component
public class TobeEntryLevelStudentSheets extends SheetGenerator {

    @Autowired
    ToBeEntryLevelStudentAnalysis toBeEntryLevelStudentAnalysis;

    @Autowired
    SubjectService subjectService;

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {
        Param param = new Param().setParameter("projectId", projectId);
        Result result = toBeEntryLevelStudentAnalysis.execute(param);
        Map<String, Object> provinceData = result.get("projectData");
        List<Map<String, Object>> schoolData = result.get("schoolData");
        List<String> subjects = subjectService.querySubjects(projectId);
        setHeader(excelWriter, subjects);
        setSecondaryHeader(excelWriter, subjects);
        fillProvinceData(excelWriter, provinceData);
        fillSchoolData(excelWriter, schoolData);
    }

    private void setHeader(ExcelWriter excelWriter, List<String> subjects) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(0, column.incrementAndGet(), "学校");
        excelWriter.set(0, column.incrementAndGet(), "人数");
        for (int i = 0; i < subjects.size(); i++) {
            excelWriter.set(0, column.incrementAndGet(), "得分率");
        }
        excelWriter.mergeCells(0, 2, 0, column.get());
    }

    private void setSecondaryHeader(ExcelWriter excelWriter, List<String> subjects) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(1, column.incrementAndGet(), "学校");
        excelWriter.mergeCells(0, column.get(), 1, column.get());
        excelWriter.set(1, column.incrementAndGet(), "人数");
        excelWriter.mergeCells(0, column.get(), 1, column.get());
        for (String subjectId : subjects) {
            excelWriter.set(1, column.incrementAndGet(), SubjectService.getSubjectName(subjectId));
        }
    }

    private void fillProvinceData(ExcelWriter excelWriter, Map<String, Object> provinceData) {
        int row = 2;
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(row, column.incrementAndGet(), provinceData.get("schoolName"));
        excelWriter.set(row, column.incrementAndGet(), provinceData.get("count"));
        List<Map<String, Object>> subjects = (List<Map<String, Object>>)provinceData.get("subjects");
        for (Map<String, Object> subjectMap : subjects) {
            excelWriter.set(row, column.incrementAndGet(), subjectMap.get("averageRate"));
        }
    }

    private void fillSchoolData(ExcelWriter excelWriter, List<Map<String, Object>> schoolData) {
        int row = 3;
        AtomicInteger column = new AtomicInteger(-1);
        for(Map<String, Object> schoolMap : schoolData){
            List<Map<String, Object>> subjects = (List<Map<String, Object>>)schoolMap.get("subjects");
            excelWriter.set(row, column.incrementAndGet(), schoolMap.get("schoolName"));
            excelWriter.set(row, column.incrementAndGet(), schoolMap.get("count"));
            for(Map<String, Object> subjectMap : subjects) {
                excelWriter.set(row, column.incrementAndGet(), subjectMap.get("averageRate"));
            }
            row++;
            column.set(-1);
        }
    }
}
