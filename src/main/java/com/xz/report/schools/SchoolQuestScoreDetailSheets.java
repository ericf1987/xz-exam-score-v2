package com.xz.report.schools;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.api.server.school.SchoolQuestScoreDetailAnalysis;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.report.SheetGenerator;
import com.xz.report.SheetTask;
import com.xz.report.classes.ClassQuestScoreDetailSheets;
import com.xz.services.TargetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author by fengye on 2016/7/1.
 */
@SuppressWarnings("unchecked")
@Component
public class SchoolQuestScoreDetailSheets extends SheetGenerator {
    @Autowired
    SchoolQuestScoreDetailAnalysis schoolQuestScoreDetailAnalysis;

    @Autowired
    TargetService targetService;

    @Autowired
    ClassQuestScoreDetailSheets classQuestScoreDetailSheets;

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {
        Target target = sheetTask.get("target");
        String subjectId = target.getId().toString();
        Range schoolRange = sheetTask.getRange();
        Param param = new Param().
                setParameter("projectId", projectId).
                setParameter("subjectId", subjectId).
                setParameter("schoolId", schoolRange.getId());
        Result result = schoolQuestScoreDetailAnalysis.execute(param);
        setupHeader(excelWriter, result);
        fillData(excelWriter, result);
    }

    public void setupHeader(ExcelWriter excelWriter, Result result) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(0, column.incrementAndGet(), "学校");
        excelWriter.set(0, column.incrementAndGet(), "班级");
        excelWriter.set(0, column.incrementAndGet(), "姓名");
        excelWriter.set(0, column.incrementAndGet(), "总分");
        excelWriter.set(0, column.incrementAndGet(), "客观题总分");
        excelWriter.set(0, column.incrementAndGet(), "主观题总分");
        List<Map<String, Object>> quests = result.get("questList");
        for(Map<String, Object> quest : quests){
            String questName = Boolean.valueOf(quest.get("isObjective").toString()) ? "客观题" + quest.get("questNo") : "主观题" + quest.get("questNo");
            excelWriter.set(0, column.incrementAndGet(), questName);
        }
    }

    public void fillData(ExcelWriter excelWriter, Result result) {
        int row = 1;
        AtomicInteger column = new AtomicInteger(-1);
        List<Map<String, Object>> studentList = classQuestScoreDetailSheets.getTotalScore(result.get("studentList"));

        //按总分排序
        Collections.sort(studentList, (Map<String, Object> m1 , Map<String, Object> m2) ->
                new Double(m2.get("questScore").toString()).compareTo(new Double(m1.get("questScore").toString()))
        );

        for(Map<String, Object> student : studentList){
            excelWriter.set(row, column.incrementAndGet(), student.get("schoolName"));
            excelWriter.set(row, column.incrementAndGet(), student.get("studentName"));
            excelWriter.set(row, column.incrementAndGet(), student.get("className"));
            excelWriter.set(row, column.incrementAndGet(), student.get("questScore"));
            excelWriter.set(row, column.incrementAndGet(), student.get("objectiveScore"));
            excelWriter.set(row, column.incrementAndGet(), student.get("subjectiveScore"));
            List<Map<String, Object>> questList = (List<Map<String, Object>>)student.get("quests");
            for(Map<String, Object> quest : questList){
                excelWriter.set(row, column.incrementAndGet(), quest.get("score"));
            }
            row++;
            column.set(-1);
        }
    }
}