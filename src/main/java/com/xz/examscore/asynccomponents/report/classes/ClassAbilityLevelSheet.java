package com.xz.examscore.asynccomponents.report.classes;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.server.classes.ClassAbilityLevelAnalysis;
import com.xz.examscore.asynccomponents.report.SheetGenerator;
import com.xz.examscore.asynccomponents.report.SheetTask;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author by fengye on 2016/6/30.
 * 班级成绩分析-试卷分析-能力层级统计
 */
@SuppressWarnings("unchecked")
@Component
public class ClassAbilityLevelSheet extends SheetGenerator {
    @Autowired
    ClassAbilityLevelAnalysis classAbilityLevelAnalysis;

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {
        Target target = sheetTask.get("target");
        String subjectId = target.getId().toString();
        Range classRange = sheetTask.getRange();
        Param param = new Param().setParameter("projectId", projectId)
                .setParameter("subjectId", subjectId)
                .setParameter("classId", classRange.getId());
        Result result = classAbilityLevelAnalysis.execute(param);
        setupHeader(excelWriter, result);
        fillClassData(excelWriter, result);
        fillStudentData(excelWriter, result);
    }

    private void setupHeader(ExcelWriter excelWriter, Result result) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(0, column.incrementAndGet(), "考号");
        excelWriter.set(0, column.incrementAndGet(), "学校考号");
        excelWriter.set(0, column.incrementAndGet(), "双向细目");
        List<Map<String, Object>> classes = result.get("classes");
        for(Map<String, Object> levelStat : classes){
            excelWriter.set(0, column.incrementAndGet(), "能力层级" + levelStat.get("levelName"));
        }
    }

    private void fillClassData(ExcelWriter excelWriter, Result result) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(1, column.incrementAndGet(), "考号");
        excelWriter.mergeCells(0, 0, 1, 0);
        excelWriter.set(1, column.incrementAndGet(), "学校考号");
        excelWriter.mergeCells(0, 1, 1, 1);
        excelWriter.set(1, column.incrementAndGet(), "本班");
        List<Map<String, Object>> classes = result.get("classes");
        for(Map<String, Object> levelStat : classes){
            excelWriter.set(1, column.incrementAndGet(), levelStat.get("score"));
        }
    }

    private void fillStudentData(ExcelWriter excelWriter, Result result) {
        int row = 2;
        AtomicInteger column = new AtomicInteger(-1);
        List<Map<String, Object>> students = result.get("students");
        for(Map<String, Object> student : students){
            excelWriter.set(row, column.incrementAndGet(), student.get("examNo"));
            excelWriter.set(row, column.incrementAndGet(), student.get("customExamNo"));
            excelWriter.set(row, column.incrementAndGet(), student.get("studentName"));
            List<Map<String, Object>> levelStats = (List<Map<String, Object>>)student.get("levelStats");
            for(Map<String, Object> levelStat : levelStats){
                excelWriter.set(row, column.incrementAndGet(), levelStat.get("score"));
            }
            row++;
            column.set(-1);
        }
    }
}
