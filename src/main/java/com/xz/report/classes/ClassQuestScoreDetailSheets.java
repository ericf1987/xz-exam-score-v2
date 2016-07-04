package com.xz.report.classes;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.api.server.classes.ClassQuestScoreDetailAnalysis;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.report.SheetGenerator;
import com.xz.report.SheetTask;
import com.xz.services.TargetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author by fengye on 2016/7/1.
 */
@SuppressWarnings("unchecked")
@Component
public class ClassQuestScoreDetailSheets extends SheetGenerator {
    @Autowired
    ClassQuestScoreDetailAnalysis classQuestScoreDetailAnalysis;

    @Autowired
    TargetService targetService;

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {
        Target target = sheetTask.get("target");
        String subjectId = target.getId().toString();
        Range classRange = sheetTask.getRange();
        Param param = new Param().
                setParameter("projectId", projectId).
                setParameter("subjectId", subjectId).
                setParameter("classId", classRange.getId());
        Result result = classQuestScoreDetailAnalysis.execute(param);
        System.out.println("班级学生各科明细-->" + result.getData());
        setupHeader(excelWriter, result);
        fillData(excelWriter, result);
    }

    private void setupHeader(ExcelWriter excelWriter, Result result) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(0, column.incrementAndGet(), "班级");
        excelWriter.set(0, column.incrementAndGet(), "姓名");
        List<Map<String, Object>> quests = result.get("questList");
        for(Map<String, Object> quest : quests){
            String questName = Boolean.valueOf(quest.get("isObjective").toString()).booleanValue() ? "客观题" + quest.get("questNo") : "主观题" + quest.get("questNo");
            excelWriter.set(0, column.incrementAndGet(), questName);
        }
    }

    private void fillData(ExcelWriter excelWriter, Result result) {
        int row = 1;
        AtomicInteger column = new AtomicInteger(-1);
        List<Map<String, Object>> studentList = result.get("studentList");
        for(Map<String, Object> student : studentList){
            excelWriter.set(row, column.incrementAndGet(), student.get("className"));
            excelWriter.set(row, column.incrementAndGet(), student.get("studentName"));
            List<Map<String, Object>> questList = (List<Map<String, Object>>)student.get("quests");
            for(Map<String, Object> quest : questList){
                excelWriter.set(row, column.incrementAndGet(), quest.get("score"));
            }
            row++;
            column.set(-1);
        }
    }
}
