package com.xz.report.classes;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.api.server.classes.ClassBasicDataAnalysis;
import com.xz.bean.Range;
import com.xz.report.SheetGenerator;
import com.xz.report.SheetTask;
import com.xz.services.ClassService;
import com.xz.services.SchoolService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author by fengye on 2016/6/24.
 */
@Component
public class ClassBasicDataSheets extends SheetGenerator {

    @Autowired
    ClassBasicDataAnalysis classBasicDataAnalysis;

    @Autowired
    SchoolService schoolService;

    @Autowired
    ClassService classService;

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {
        Range classRange = sheetTask.getRange();
        Document doc = classService.findClass(projectId, classRange.getId());
        String schoolId = doc.getString("school");
        Param param = new Param().setParameter("projectId", projectId).
                setParameter("schoolId", schoolId).
                setParameter("classId", classRange.getId());
        //设置表头
        Result result = classBasicDataAnalysis.execute(param);
        List<Map<String, Object>> studentBasicData = result.get("studentBasicData");
        setupHeader(excelWriter, studentBasicData);
        setupSecondaryHeader(excelWriter, studentBasicData);
        fillSchoolData(excelWriter, studentBasicData);
    }

    private void setupHeader(ExcelWriter excelWriter, List<Map<String, Object>> studentBasicData) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(0, column.incrementAndGet(), "姓名");
        excelWriter.set(0, column.incrementAndGet(), "班级");
        excelWriter.set(0, column.incrementAndGet(), "全科");
        column.incrementAndGet();
        column.incrementAndGet();
        column.incrementAndGet();
        excelWriter.mergeCells(0, column.get() - 3, 0, column.get());
        List<Map<String, Object>> subjects = getSubjects(studentBasicData);
        for (Map<String, Object> subject : subjects) {
            String subjectName = subject.get("subjectName").toString();
            excelWriter.set(0, column.incrementAndGet(), subjectName);
            column.incrementAndGet();
            column.incrementAndGet();
            column.incrementAndGet();
            excelWriter.mergeCells(0, column.get() - 3, 0, column.get());
        }
    }

    private void setupSecondaryHeader(ExcelWriter excelWriter, List<Map<String, Object>> studentBasicData) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(1, column.incrementAndGet(), "姓名");
        excelWriter.mergeCells(0, column.get(), 1, column.get());
        excelWriter.set(1, column.incrementAndGet(), "班级");
        excelWriter.mergeCells(0, column.get(), 1, column.get());
        excelWriter.set(1, column.incrementAndGet(), "得分");
        excelWriter.set(1, column.incrementAndGet(), "总排名");
        excelWriter.set(1, column.incrementAndGet(), "学校排名");
        excelWriter.set(1, column.incrementAndGet(), "班级排名");
        List<Map<String, Object>> subjects = getSubjects(studentBasicData);
        for (int i = 0; i < subjects.size(); i++) {
            excelWriter.set(1, column.incrementAndGet(), "得分");
            excelWriter.set(1, column.incrementAndGet(), "总排名");
            excelWriter.set(1, column.incrementAndGet(), "学校排名");
            excelWriter.set(1, column.incrementAndGet(), "班级排名");
        }
    }

    private void fillSchoolData(ExcelWriter excelWriter, List<Map<String, Object>> studentBasicData) {
        int row = 2;
        AtomicInteger column = new AtomicInteger(-1);
        for (Map<String, Object> one : studentBasicData) {
            excelWriter.set(row, column.incrementAndGet(), one.get("studentName"));
            excelWriter.set(row, column.incrementAndGet(), one.get("class"));
            //全科数据
            Map<String, Object> projectAnalysis = (Map<String, Object>) one.get("projectAnalysis");
            excelWriter.set(row, column.incrementAndGet(), projectAnalysis.get("score"));
            excelWriter.set(row, column.incrementAndGet(), projectAnalysis.get("totalRankIndex"));
            excelWriter.set(row, column.incrementAndGet(), projectAnalysis.get("schoolRankIndex"));
            excelWriter.set(row, column.incrementAndGet(), projectAnalysis.get("classRankIndex"));
            List<Map<String, Object>> subjects = (List<Map<String, Object>>)one.get("subjectAnalysis");
            for (Map<String, Object> subject : subjects) {
                excelWriter.set(row, column.incrementAndGet(), subject.get("score"));
                excelWriter.set(row, column.incrementAndGet(), subject.get("totalRankIndex"));
                excelWriter.set(row, column.incrementAndGet(), subject.get("schoolRankIndex"));
                excelWriter.set(row, column.incrementAndGet(), subject.get("classRankIndex"));
            }
            row++;
            column.set(-1);
        }
    }

    private List<Map<String, Object>> getSubjects(List<Map<String, Object>> studentBasicData) {
        Map<String, Object> one = studentBasicData.get(0);
        List<Map<String, Object>> subjects = (List<Map<String, Object>>) one.get("subjectAnalysis");
        return subjects;
    }
}
