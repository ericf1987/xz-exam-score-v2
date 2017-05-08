package com.xz.examscore.asynccomponents.report.customization.examAlliance;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.ajia.Param;
import com.xz.examscore.api.server.customization.examAlliance.SubjectTopAverageAnalysis;
import com.xz.examscore.asynccomponents.report.SheetGenerator;
import com.xz.examscore.asynccomponents.report.SheetTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author by fengye on 2016/12/30.
 */
@Component
public class SubjectTopAverageSheets extends SheetGenerator {

    @Autowired
    SubjectTopAverageAnalysis subjectTopAverageAnalysis;

    public static final String[] SECONDARY_COLUMN = new String[]{
            "学校", "平均分", "差值"
    };

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {
        Param param = new Param().setParameter("projectId", projectId);
        Result result = subjectTopAverageAnalysis.execute(param);
        setHeader(excelWriter);
        setSecondaryHeader(excelWriter);
        fillData(excelWriter, result);
    }

    private void setHeader(ExcelWriter excelWriter) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(0, column.incrementAndGet(), "科目");
        excelWriter.set(0, column.incrementAndGet(), "最高分");
        excelWriter.set(0, column.incrementAndGet(), "平均分");
        excelWriter.set(0, column.incrementAndGet(), "得分率");
        for(int i = 0; i < SECONDARY_COLUMN.length;i++){
            excelWriter.set(0, column.incrementAndGet(), "平均分最高的学校");
        }
        excelWriter.mergeCells(0, column.get() - SECONDARY_COLUMN.length + 1, 0, column.get());
    }

    private void setSecondaryHeader(ExcelWriter excelWriter) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(1, column.incrementAndGet(), "科目");
        excelWriter.mergeCells(0, column.get(), 1, column.get());
        excelWriter.set(1, column.incrementAndGet(), "最高分");
        excelWriter.mergeCells(0, column.get(), 1, column.get());
        excelWriter.set(1, column.incrementAndGet(), "平均分");
        excelWriter.mergeCells(0, column.get(), 1, column.get());
        excelWriter.set(1, column.incrementAndGet(), "得分率");
        excelWriter.mergeCells(0, column.get(), 1, column.get());
        for(int i = 0; i < SECONDARY_COLUMN.length;i++){
            excelWriter.set(1, column.incrementAndGet(), SECONDARY_COLUMN[i]);
        }
    }

    private void fillData(ExcelWriter excelWriter, Result result) {
        int row = 2;
        AtomicInteger column = new AtomicInteger(-1);
        List<Map<String, Object>> subjects = result.get("subjects");
        for(Map<String, Object> subjectMap : subjects){
            row = fillOneRow(excelWriter, row, column, subjectMap);
        }
        List<Map<String, Object>> subjectCombinations = result.get("subjectCombinations");
        for(Map<String, Object> subjectMap : subjectCombinations){
            row = fillOneRow(excelWriter, row, column, subjectMap);
        }
    }

    private int fillOneRow(ExcelWriter excelWriter, int row, AtomicInteger column, Map<String, Object> subjectMap) {
        excelWriter.set(row, column.incrementAndGet(), subjectMap.get("subjectName"));
        excelWriter.set(row, column.incrementAndGet(), subjectMap.get("maxScore"));
        excelWriter.set(row, column.incrementAndGet(), subjectMap.get("average"));
        excelWriter.set(row, column.incrementAndGet(), subjectMap.get("scoreRate"));
        Map<String, Object> topSchool = (Map<String, Object>)subjectMap.get("topSchool");
        excelWriter.set(row, column.incrementAndGet(), topSchool.get("schoolName"));
        excelWriter.set(row, column.incrementAndGet(), topSchool.get("topAverage"));
        excelWriter.set(row, column.incrementAndGet(), subjectMap.get("subAverage"));
        row++;
        column.set(-1);
        return row;
    }
}
