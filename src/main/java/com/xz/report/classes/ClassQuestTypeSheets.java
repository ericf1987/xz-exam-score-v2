package com.xz.report.classes;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.api.server.classes.ClassQuestTypeAnalysis;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.report.SheetGenerator;
import com.xz.report.SheetTask;
import com.xz.services.ClassService;
import com.xz.services.SchoolService;
import com.xz.util.DoubleUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author by fengye on 2016/6/27.
 * 班级成绩分析-试卷分析-题型分析
 */
@SuppressWarnings("unchecked")
@Component
public class ClassQuestTypeSheets extends SheetGenerator {

    @Autowired
    ClassQuestTypeAnalysis classQuestTypeAnalysis;

    @Autowired
    SchoolService schoolService;

    @Autowired
    ClassService classService;

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {
        Target subjectTarget = sheetTask.get("target");
        Range classRange = sheetTask.getRange();
        Param param = new Param().setParameter("projectId", projectId).
                setParameter("subjectId", subjectTarget.getId().toString()).
                setParameter("classId", classRange.getId());
        //设置表头
        Result result = classQuestTypeAnalysis.execute(param);
        setupHeader(excelWriter, result);
        fillClassData(excelWriter, result);
        fillStuData(excelWriter, result);
    }

    private void setupHeader(ExcelWriter excelWriter, Result result) {
        AtomicInteger column = new AtomicInteger(-1);
        List<Map<String, Object>> classes = result.get("classes");
        excelWriter.set(0, column.incrementAndGet(), "考号");
        excelWriter.set(0, column.incrementAndGet(), "题型");
        for (Map<String, Object> clazz : classes) {
            excelWriter.set(0, column.incrementAndGet(), clazz.get("name"));
        }
    }

    private void fillClassData(ExcelWriter excelWriter, Result result) {
        AtomicInteger column = new AtomicInteger(-1);
        List<Map<String, Object>> classes = result.get("classes");
        excelWriter.set(1, column.incrementAndGet(), "考号");
        excelWriter.mergeCells(0, 0, 1, 0);
        excelWriter.set(1, column.incrementAndGet(), "本班");
        for (Map<String, Object> clazz : classes) {
            excelWriter.set(1, column.incrementAndGet(), DoubleUtils.toPercent(Double.parseDouble(clazz.get("scoreRate").toString())));
        }
    }

    private void fillStuData(ExcelWriter excelWriter, Result result) {
        int row = 2;
        AtomicInteger column = new AtomicInteger(-1);
        List<Map<String, Object>> students = result.get("students");
        for (Map<String, Object> student : students) {
            excelWriter.set(row, column.incrementAndGet(), student.get("examNo"));
            excelWriter.set(row, column.incrementAndGet(), student.get("studentName"));
            List<Map<String, Object>> questTypes = (List<Map<String, Object>>) student.get("questTypes");
            for (Map<String, Object> questType : questTypes) {
                excelWriter.set(row, column.incrementAndGet(), DoubleUtils.toPercent(Double.parseDouble(questType.get("scoreRate").toString())));
            }
            row++;
            column.set(-1);
        }
    }
}
