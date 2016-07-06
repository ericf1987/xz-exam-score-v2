package com.xz.report.schools;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.api.server.school.SchoolPointAnalysis;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.report.SheetGenerator;
import com.xz.report.SheetTask;
import com.xz.services.SchoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author by fengye on 2016/7/5.
 */
@SuppressWarnings("unchecked")
@Component
public class SchoolPointSheets extends SheetGenerator {

    @Autowired
    SchoolPointAnalysis schoolPointAnalysis;

    @Autowired
    SchoolService schoolService;

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {
        Target target = sheetTask.get("target");
        String subjectId = target.getId().toString();
        Range schoolRange = sheetTask.getRange();
        Param param = new Param().setParameter("projectId", projectId)
                .setParameter("subjectId", subjectId)
                .setParameter("schoolId", schoolRange.getId());
        Result result = schoolPointAnalysis.execute(param);
        setupHeader(excelWriter, result);
        fillClassData(excelWriter, result);
        fillStudentData(excelWriter, result);
    }

    private void setupHeader(ExcelWriter excelWriter, Result result) {
        AtomicInteger column = new AtomicInteger(-1);
        List<Map<String, Object>> schools = result.get("schools");
        excelWriter.set(0, column.incrementAndGet(), "题型");
        excelWriter.set(0, column.incrementAndGet(), "班级");
        for(Map<String, Object> pointstat : schools){
            excelWriter.set(0, column.incrementAndGet(), pointstat.get("pointName"));
        }
    }

    private void fillClassData(ExcelWriter excelWriter, Result result) {
        AtomicInteger column = new AtomicInteger(-1);
        List<Map<String, Object>> classes = result.get("schools");
        excelWriter.set(1, column.incrementAndGet(), "本校");
        excelWriter.set(1, column.incrementAndGet(), "本校");
        for(Map<String, Object> pointstat : classes){
            excelWriter.set(1, column.incrementAndGet(), pointstat.get("score"));
        }
    }

    private void fillStudentData(ExcelWriter excelWriter, Result result) {
        int row = 2;
        AtomicInteger column = new AtomicInteger(-1);
        List<Map<String, Object>> students = result.get("students");
        for(Map<String, Object> student : students){
            List<Map<String, Object>> pointStats = (List<Map<String, Object>>)student.get("pointStats");
            excelWriter.set(row, column.incrementAndGet(), student.get("studentName"));
            excelWriter.set(row, column.incrementAndGet(), student.get("className"));
            for(Map<String, Object> pointStat : pointStats) {
                excelWriter.set(row, column.incrementAndGet(), pointStat.get("score"));
            }
            row++;
            column.set(-1);
        }
    }
}
