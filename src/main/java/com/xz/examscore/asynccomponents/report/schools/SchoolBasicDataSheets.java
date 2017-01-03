package com.xz.examscore.asynccomponents.report.schools;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.server.school.SchoolBasicDataAnalysis;
import com.xz.examscore.asynccomponents.report.SheetGenerator;
import com.xz.examscore.asynccomponents.report.SheetTask;
import com.xz.examscore.bean.Range;
import com.xz.examscore.services.SchoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author by fengye on 2016/6/24.
 * 学校成绩分析-基础数据-学生各科成绩明细
 */
@SuppressWarnings("unchecked")
@Component
public class SchoolBasicDataSheets extends SheetGenerator {
    @Autowired
    SchoolBasicDataAnalysis schoolBasicDataAnalysis;

    @Autowired
    SchoolService schoolService;

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {
        Range schoolRange = sheetTask.getRange();
        Param param = new Param().setParameter("projectId", projectId).
                setParameter("schoolId", schoolRange.getId());
        //设置表头
        Result result = schoolBasicDataAnalysis.execute(param);
        List<Map<String, Object>> studentBasicData = result.get("studentBasicData");
        setupHeader(excelWriter, studentBasicData);
        setupSecondaryHeader(excelWriter, studentBasicData);
        fillSchoolData(excelWriter, studentBasicData);
    }

    private void setupHeader(ExcelWriter excelWriter, List<Map<String, Object>> studentBasicData) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(0, column.incrementAndGet(), "考号");
        excelWriter.set(0, column.incrementAndGet(), "学校考号");
        excelWriter.set(0, column.incrementAndGet(), "姓名");
        excelWriter.set(0, column.incrementAndGet(), "学校");
        excelWriter.set(0, column.incrementAndGet(), "班级");
        excelWriter.set(0, column.incrementAndGet(), "全科");
        column.incrementAndGet();
        column.incrementAndGet();
        excelWriter.mergeCells(0, column.get() - 2, 0, column.get());
        List<Map<String, Object>> subjects = getSubjects(studentBasicData);
        for (Map<String, Object> subject : subjects) {
            String subjectName = subject.get("subjectName").toString();
            excelWriter.set(0, column.incrementAndGet(), subjectName);
            column.incrementAndGet();
            column.incrementAndGet();
            excelWriter.mergeCells(0, column.get() - 2, 0, column.get());
        }
    }

    private void setupSecondaryHeader(ExcelWriter excelWriter, List<Map<String, Object>> studentBasicData) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(1, column.incrementAndGet(), "考号");
        excelWriter.mergeCells(0, column.get(), 1, column.get());
        excelWriter.set(1, column.incrementAndGet(), "学校");
        excelWriter.mergeCells(0, column.get(), 1, column.get());
        excelWriter.set(1, column.incrementAndGet(), "姓名");
        excelWriter.mergeCells(0, column.get(), 1, column.get());
        excelWriter.set(1, column.incrementAndGet(), "学校");
        excelWriter.mergeCells(0, column.get(), 1, column.get());
        excelWriter.set(1, column.incrementAndGet(), "班级");
        excelWriter.mergeCells(0, column.get(), 1, column.get());
        excelWriter.set(1, column.incrementAndGet(), "得分");
        excelWriter.set(1, column.incrementAndGet(), "总排名");
        excelWriter.set(1, column.incrementAndGet(), "学校排名");
        List<Map<String, Object>> subjects = getSubjects(studentBasicData);
        for (int i = 0; i < subjects.size(); i++) {
            excelWriter.set(1, column.incrementAndGet(), "得分");
            excelWriter.set(1, column.incrementAndGet(), "总排名");
            excelWriter.set(1, column.incrementAndGet(), "学校排名");
        }
    }

    private void fillSchoolData(ExcelWriter excelWriter, List<Map<String, Object>> studentBasicData) {
        int row = 2;
        AtomicInteger column = new AtomicInteger(-1);
        for (Map<String, Object> one : studentBasicData) {
            excelWriter.set(row, column.incrementAndGet(), one.get("examNo"));
            excelWriter.set(row, column.incrementAndGet(), one.get("customExamNo"));
            excelWriter.set(row, column.incrementAndGet(), one.get("studentName"));
            excelWriter.set(row, column.incrementAndGet(), one.get("school"));
            excelWriter.set(row, column.incrementAndGet(), one.get("class"));
            //全科数据
            Map<String, Object> projectAnalysis = (Map<String, Object>) one.get("projectAnalysis");
            excelWriter.set(row, column.incrementAndGet(), projectAnalysis.get("score"));
            excelWriter.set(row, column.incrementAndGet(), projectAnalysis.get("totalRankIndex"));
            excelWriter.set(row, column.incrementAndGet(), projectAnalysis.get("schoolRankIndex"));
            List<Map<String, Object>> subjects = (List<Map<String, Object>>)one.get("subjectAnalysis");
            for (Map<String, Object> subject : subjects) {
                excelWriter.set(row, column.incrementAndGet(), subject.get("score"));
                excelWriter.set(row, column.incrementAndGet(), subject.get("totalRankIndex"));
                excelWriter.set(row, column.incrementAndGet(), subject.get("schoolRankIndex"));
            }
            row++;
            column.set(-1);
        }
    }

    private List<Map<String, Object>> getSubjects(List<Map<String, Object>> studentBasicData) {
        if(studentBasicData.isEmpty()){
            return Collections.emptyList();
        }
        Map<String, Object> one = studentBasicData.get(0);
        return (List<Map<String, Object>>) one.get("subjectAnalysis");
    }
}
