package com.xz.examscore.asynccomponents.report.customization;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.ajia.Param;
import com.xz.examscore.api.server.customization.StudentProjectRankAndScore;
import com.xz.examscore.asynccomponents.report.SheetGenerator;
import com.xz.examscore.asynccomponents.report.SheetTask;
import com.xz.examscore.bean.Target;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author by fengye on 2017/4/11.
 */
@Component
public class StudentProjectRankAndScoreSheets extends SheetGenerator {

    @Autowired
    StudentProjectRankAndScore studentProjectRankAndScore;

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {
        Param param = new Param().setParameter("projectId", projectId);
        Result result = studentProjectRankAndScore.execute(param);
        List<Map<String, Object>> students = result.get("students");
        setHeader(excelWriter);
        fillData(excelWriter, students);
    }

    private void setHeader(ExcelWriter excelWriter) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(0, column.incrementAndGet(), "学校名称");
        excelWriter.set(0, column.incrementAndGet(), "班级名称");
        excelWriter.set(0, column.incrementAndGet(), "学生ID");
        excelWriter.set(0, column.incrementAndGet(), "学生姓名");
        excelWriter.set(0, column.incrementAndGet(), "总得分");
        excelWriter.set(0, column.incrementAndGet(), "总排名");
    }

    private void fillData(ExcelWriter excelWriter, List<Map<String, Object>> students) {
        AtomicInteger column = new AtomicInteger(-1);
        int row = 1;
        for(Map<String, Object> one : students){
            excelWriter.set(row, column.incrementAndGet(), one.get("schoolName"));
            excelWriter.set(row, column.incrementAndGet(), one.get("className"));
            excelWriter.set(row, column.incrementAndGet(), one.get("studentId"));
            excelWriter.set(row, column.incrementAndGet(), one.get("studentName"));
            excelWriter.set(row, column.incrementAndGet(), one.get("totalScore"));
            excelWriter.set(row, column.incrementAndGet(), one.get("totalRank"));
            row++;
            column.set(-1);
        }
    }
}
