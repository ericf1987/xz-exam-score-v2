package com.xz.examscore.asynccomponents.report.schools;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.server.school.SchoolCollegeEntryLevelAnalysis;
import com.xz.examscore.asynccomponents.report.SheetGenerator;
import com.xz.examscore.asynccomponents.report.SheetTask;
import com.xz.examscore.asynccomponents.report.total.TotalCollegeEntryLevelSheets;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.CollegeEntryLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author by fengye on 2016/11/4.
 */
@Component
public class SchoolCollegeEntryLevelSheets extends SheetGenerator {

    @Autowired
    SchoolCollegeEntryLevelAnalysis schoolCollegeEntryLevelAnalysis;

    @Autowired
    CollegeEntryLevelService collegeEntryLevelService;

    @Autowired
    TotalCollegeEntryLevelSheets totalCollegeEntryLevelSheets;

    public static final String[] SECONDARY_HEADER = new String[]{
            "成绩", "排名"
    };

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {
        Range range = sheetTask.getRange();
        Target target = sheetTask.getTarget();
        int count = collegeEntryLevelService.getEntryLevelStudentCount(projectId, range, target, "");
        String[] rankSegment = new String[]{
                "1", String.valueOf(count)
        };
        Param param = new Param().setParameter("projectId", projectId).setParameter("schoolId", range.getId())
                .setParameter("rankSegment", rankSegment);
        Result result = schoolCollegeEntryLevelAnalysis.execute(param);
        List<Map<String, Object>> students = (List<Map<String, Object>>)result.get("students");
        setHeader(excelWriter, students);
        setSecondaryHeader(excelWriter, students);
        fillData(excelWriter, students);
    }

    private void setHeader(ExcelWriter excelWriter, List<Map<String, Object>> students) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(0, column.incrementAndGet(), "班级名称");
        excelWriter.set(0, column.incrementAndGet(), "学生考号");
        excelWriter.set(0, column.incrementAndGet(), "学校考号");
        excelWriter.set(0, column.incrementAndGet(), "学生姓名");
        excelWriter.set(0, column.incrementAndGet(), "上线情况");
        if(null != students && !students.isEmpty()){
            Map<String, Object> one = students.get(0);
            List<Map<String, Object>> subjects = (List<Map<String, Object>>)one.get("subjects");
            subjects.forEach(subject -> {
                excelWriter.set(0, column.incrementAndGet(), subject.get("subjectName"));
                excelWriter.mergeCells(0, column.get(), 0, column.incrementAndGet());
            });
        }
    }

    private void setSecondaryHeader(ExcelWriter excelWriter, List<Map<String, Object>> students) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(1, column.incrementAndGet(), "班级名称");
        excelWriter.mergeCells(0, column.get(), 1, column.get());
        excelWriter.set(1, column.incrementAndGet(), "学生考号");
        excelWriter.mergeCells(0, column.get(), 1, column.get());
        excelWriter.set(1, column.incrementAndGet(), "学校考号");
        excelWriter.mergeCells(0, column.get(), 1, column.get());
        excelWriter.set(1, column.incrementAndGet(), "学生姓名");
        excelWriter.mergeCells(0, column.get(), 1, column.get());
        excelWriter.set(1, column.incrementAndGet(), "上线情况");
        excelWriter.mergeCells(0, column.get(), 1, column.get());
        totalCollegeEntryLevelSheets.appendSubjectsColumns(excelWriter, students, column);
    }

    private void fillData(ExcelWriter excelWriter, List<Map<String, Object>> students) {
        AtomicInteger column = new AtomicInteger(-1);
        int row = 2;
        for(Map<String, Object> one : students){
            excelWriter.set(row, column.incrementAndGet(), one.get("className"));
            excelWriter.set(row, column.incrementAndGet(), one.get("examNo"));
            excelWriter.set(row, column.incrementAndGet(), one.get("customExamNo"));
            excelWriter.set(row, column.incrementAndGet(), one.get("name"));
            excelWriter.set(row, column.incrementAndGet(), one.get("entryLevelInfo"));
            List<Map<String, Object>> subjects = (List<Map<String, Object>>)one.get("subjects");
            for (Map<String, Object> subject : subjects) {
                excelWriter.set(row, column.incrementAndGet(), subject.get("score"));
                excelWriter.set(row, column.incrementAndGet(), subject.get("rankIndex"));
            }
            column.set(-1);
            row++;
        }
    }
}
