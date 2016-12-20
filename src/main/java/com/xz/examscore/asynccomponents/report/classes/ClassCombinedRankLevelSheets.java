package com.xz.examscore.asynccomponents.report.classes;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.server.classes.ClassCombinedRankLevelAnalysis;
import com.xz.examscore.asynccomponents.report.SheetGenerator;
import com.xz.examscore.asynccomponents.report.SheetTask;
import com.xz.examscore.services.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author by fengye on 2016/12/20.
 */
@SuppressWarnings("ALL")
@Component
public class ClassCombinedRankLevelSheets extends SheetGenerator {

    @Autowired
    ClassCombinedRankLevelAnalysis classCombinedRankLevelAnalysis;

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {
        String classId = sheetTask.getRange().getId();
        Param param = new Param().setParameter("projectId", projectId).
                setParameter("classId", classId);
        Result result = classCombinedRankLevelAnalysis.execute(param);
        setHeader(excelWriter, result);
        setSecondaryHeader(excelWriter, result);
        fillData(excelWriter, result);
    }

    private void setHeader(ExcelWriter excelWriter, Result result) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(0, column.incrementAndGet(), "考号");
        excelWriter.set(0, column.incrementAndGet(), "姓名");
        List<String> subjectIds = result.get("subjectIds");
        for (String subjectId : subjectIds){
            excelWriter.set(0, column.incrementAndGet(), SubjectService.getSubjectName(subjectId));
            excelWriter.mergeCells(0, column.get(), 0, column.incrementAndGet());
        }
        excelWriter.set(0, column.incrementAndGet(), "合计");
    }

    private void setSecondaryHeader(ExcelWriter excelWriter, Result result) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(1, column.incrementAndGet(), "考号");
        excelWriter.mergeCells(0, column.get(), 1, column.get());
        excelWriter.set(1, column.incrementAndGet(), "姓名");
        excelWriter.mergeCells(0, column.get(), 1, column.get());
        List<String> subjectList = result.get("subjectIds");
        for(int i = 0;i < subjectList.size();i++){
            excelWriter.set(1, column.incrementAndGet(), "分数");
            excelWriter.set(1, column.incrementAndGet(), "等第");
        }
        excelWriter.set(1, column.incrementAndGet(), "合计");
        excelWriter.mergeCells(0, column.get(), 1, column.get());
    }

    private void fillData(ExcelWriter excelWriter, Result result) {
        int row = 2;
        AtomicInteger column = new AtomicInteger(-1);
        List<Map<String, Object>> students = result.get("students");
        for(Map<String, Object> student : students){
            List<Map<String, Object>> subjectList = (List<Map<String, Object>>)student.get("subjects");
            excelWriter.set(row, column.incrementAndGet(), student.get("examNo"));
            excelWriter.set(row, column.incrementAndGet(), student.get("studentName"));
            for(Map<String, Object> subject : subjectList){
                excelWriter.set(row, column.incrementAndGet(), subject.get("score"));
                excelWriter.set(row, column.incrementAndGet(), subject.get("rankLevel"));
            }
            excelWriter.set(row, column.incrementAndGet(), student.get("totalRankLevel"));
            row++;
            column.set(-1);
        }
    }
}
