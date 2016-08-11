package com.xz.report.classes;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.api.server.classes.ClassRankLevelAnalysis;
import com.xz.report.SheetGenerator;
import com.xz.report.SheetTask;
import com.xz.services.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author by fengye on 2016/7/21.
 * 班级成绩分析-基础分析-等第统计
 */
@SuppressWarnings("unchecked")
@Component
public class ClassRankLevelSheets extends SheetGenerator {

    @Autowired
    ClassRankLevelAnalysis classRankLevelAnalysis;

    @Autowired
    SubjectService subjectService;

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {

        String classId = sheetTask.getRange().getId();
        Param param = new Param().setParameter("projectId", projectId).
                setParameter("classId", classId);

        Result result = classRankLevelAnalysis.execute(param);

        setHeader(excelWriter, result);
        setSecondaryHeader(excelWriter, result);
        fillData(excelWriter, result);

    }

    private void setHeader(ExcelWriter excelWriter, Result result) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(0, column.incrementAndGet(), "姓名");
        List<Map<String, Object>> subjectScoreList = result.get("subjectScoreList");
        for (Map<String, Object> subjectScore : subjectScoreList){
            excelWriter.set(0, column.incrementAndGet(), subjectScore.get("subjectName") + "(" + subjectScore.get("score") + ")");
            excelWriter.mergeCells(0, column.get(), 0, column.incrementAndGet());
        }
        excelWriter.set(0, column.incrementAndGet(), "合计");
    }

    private void setSecondaryHeader(ExcelWriter excelWriter, Result result) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(1, column.incrementAndGet(), "姓名");
        excelWriter.mergeCells(0, column.get(), 1, column.get());
        List<String> subjectList = result.get("subjectList");
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
        List<Map<String, Object>> studentInfos = result.get("studentInfos");
        for(Map<String, Object> studentInfo : studentInfos){
            List<Map<String, Object>> subjectList = (List<Map<String, Object>>)studentInfo.get("subject");
            excelWriter.set(row, column.incrementAndGet(), studentInfo.get("studentName"));
            for(Map<String, Object> subject : subjectList){
                excelWriter.set(row, column.incrementAndGet(), subject.get("subjectScore"));
                excelWriter.set(row, column.incrementAndGet(), subject.get("subjectRankLevel"));
            }
            excelWriter.set(row, column.incrementAndGet(), studentInfo.get("ProjectRankLevel"));
            row++;
            column.set(-1);
        }
    }
}
