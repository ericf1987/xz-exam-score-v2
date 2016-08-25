package com.xz.examscore.asynccomponents.report.schools;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.server.school.SchoolSubjectiveAnalysis;
import com.xz.examscore.asynccomponents.report.SheetGenerator;
import com.xz.examscore.asynccomponents.report.SheetTask;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.util.DoubleUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author by fengye on 2016/6/19.
 * 学校成绩分析-试卷分析-主观题分析
 */
@SuppressWarnings("unchecked")
@Component
public class SchoolPaperSubjectiveSheets extends SheetGenerator {
    @Autowired
    SchoolSubjectiveAnalysis schoolSubjectiveAnalysis;

    public static final String[] SECONDARY_COLUMN = new String[]{
            "分数", "得分率"
    };

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {
        Target target = sheetTask.get("target");
        String subjectId = target.getId().toString();
        Range schoolRange = sheetTask.getRange();
        Param param = new Param().setParameter("projectId", projectId).
                setParameter("subjectId", subjectId).
                setParameter("schoolId", schoolRange.getId());
        Result result = schoolSubjectiveAnalysis.execute(param);
        setupHeader(excelWriter, result.get("classes"));
        setupSecondaryHeader(excelWriter, result.get("classes"));
        fillData(excelWriter, result);
    }

    private void setupHeader(ExcelWriter excelWriter, List<Map<String, Object>> classes) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(0, column.incrementAndGet(), "主观题");
        excelWriter.set(0, column.incrementAndGet(), "总体");
        excelWriter.mergeCells(0, column.get(), 0, column.incrementAndGet());
        for (Map<String, Object> clazz : classes) {
            excelWriter.set(0, column.incrementAndGet(), clazz.get("className"));
            excelWriter.mergeCells(0, column.get(), 0, column.incrementAndGet());
        }
    }

    private void setupSecondaryHeader(ExcelWriter excelWriter, List<Map<String, Object>> classes) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(1, column.incrementAndGet(), "主观题");
        excelWriter.mergeCells(0, column.get(), 1, column.get());
        excelWriter.set(1, column.incrementAndGet(), SECONDARY_COLUMN[0]);
        excelWriter.set(1, column.incrementAndGet(), SECONDARY_COLUMN[1]);
        for (int i = 0; i < classes.size(); i++) {
            excelWriter.set(1, column.incrementAndGet(), SECONDARY_COLUMN[0]);
            excelWriter.set(1, column.incrementAndGet(), SECONDARY_COLUMN[1]);
        }
    }

    private void fillData(ExcelWriter excelWriter, Result result) {
        int row = 2;
        AtomicInteger column = new AtomicInteger(-1);
        List<Map<String, Object>> schools = result.get("schools");
        for(int i = 0; i < schools.size(); i++){
            excelWriter.set(row, column.incrementAndGet(), schools.get(i).get("questNo"));
            excelWriter.set(row, column.incrementAndGet(), schools.get(i).get("average"));
            excelWriter.set(row, column.incrementAndGet(), DoubleUtils.toPercent(Double.valueOf(schools.get(i).get("rate").toString())));
            List<Map<String, Object>> classes = result.get("classes");
            for(Map<String, Object> clazz : classes){
                List<Map<String, Object>> subjectives = (List<Map<String, Object>>)clazz.get("subjectives");
                excelWriter.set(row, column.incrementAndGet(), subjectives.get(i).get("average"));
                excelWriter.set(row, column.incrementAndGet(), DoubleUtils.toPercent(Double.valueOf(subjectives.get(i).get("rate").toString())));
            }
            row++;
            column.set(-1);
        }
    }
}
