package com.xz.examscore.asynccomponents.report.classes;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.server.classes.ClassQuestScoreDetailAnalysis;
import com.xz.examscore.asynccomponents.report.SheetGenerator;
import com.xz.examscore.asynccomponents.report.SheetTask;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.TargetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author by fengye on 2016/7/1.
 * 班级成绩分析-基础数据-学生题目得分明细
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
        setupHeader(excelWriter, result);
        fillData(excelWriter, result);
    }

    private void setupHeader(ExcelWriter excelWriter, Result result) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(0, column.incrementAndGet(), "考号");
        excelWriter.set(0, column.incrementAndGet(), "班级");
        excelWriter.set(0, column.incrementAndGet(), "姓名");
        excelWriter.set(0, column.incrementAndGet(), "总分");
        excelWriter.set(0, column.incrementAndGet(), "客观题总分");
        excelWriter.set(0, column.incrementAndGet(), "主观题总分");
        List<Map<String, Object>> quests = result.get("questList");
        for(Map<String, Object> quest : quests){
            //在题型没有录入的情况下，会拿到null属性
            if(null != quest.get("isObjective")){
                String questName = Boolean.valueOf(quest.get("isObjective").toString()) ? "客观题" + quest.get("questNo") : "主观题" + quest.get("questNo");
                excelWriter.set(0, column.incrementAndGet(), questName);
            }else{
                excelWriter.set(0, column.incrementAndGet(), quest.get("questNo"));
            }
        }
    }

    private void fillData(ExcelWriter excelWriter, Result result) {
        int row = 1;
        AtomicInteger column = new AtomicInteger(-1);
        List<Map<String, Object>> studentList = getTotalScore(result.get("studentList"));

        //按总分排序
        Collections.sort(studentList, (Map<String, Object> m1 , Map<String, Object> m2) ->
                new Double(m2.get("questScore").toString()).compareTo(new Double(m1.get("questScore").toString()))
        );

        for(Map<String, Object> student : studentList){
            excelWriter.set(row, column.incrementAndGet(), student.get("examNo"));
            excelWriter.set(row, column.incrementAndGet(), student.get("className"));
            excelWriter.set(row, column.incrementAndGet(), student.get("studentName"));
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

    //计算学生主客观题总分
    public List<Map<String, Object>> getTotalScore(List<Map<String, Object>> studentList){
        //统计客观题总分和主观题总分
        for(Map<String, Object> student : studentList){
            List<Map<String, Object>> quests = (List<Map<String, Object>>)student.get("quests");
            //统计每个题目的得分
            double objectiveScore = 0;
            double subjectiveScore = 0;
            for(Map<String, Object> quest : quests){
                if(null != quest.get("isObjective")){
                    boolean isObjective = Boolean.parseBoolean(quest.get("isObjective").toString());
                    if(isObjective){
                        objectiveScore += Double.parseDouble(quest.get("score").toString());
                    }else{
                        subjectiveScore += Double.parseDouble(quest.get("score").toString());
                    }
                }
            }
            student.put("objectiveScore", objectiveScore);
            student.put("subjectiveScore", subjectiveScore);
            student.put("questScore", objectiveScore + subjectiveScore);
        }
        return studentList;
    }
}
