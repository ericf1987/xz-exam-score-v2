package com.xz.report.classes.compare;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.api.server.classes.compare.ClassScoreCompareAnalysis;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.report.SheetGenerator;
import com.xz.report.SheetTask;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author by fengye on 2016/7/26.
 */
@SuppressWarnings("unchecked")
@Component
public class ClassScoreCompareSheets extends SheetGenerator {

    @Autowired
    ClassScoreCompareAnalysis classScoreCompareAnalysis;

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {
        Target target = sheetTask.get("target");
        String subjectId = target.match(Target.PROJECT) ? null : target.getId().toString();
        Range classRange = sheetTask.getRange();
        Param param = new Param().setParameter("projectId", projectId).
                setParameter("subjectId", subjectId).
                setParameter("classId", classRange.getId());
        Result result = classScoreCompareAnalysis.execute(param);

        setHeader(excelWriter, result);
        fillData(excelWriter, result);
    }

    private void setHeader(ExcelWriter excelWriter, Result result) {
        AtomicInteger column = new AtomicInteger(-1);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = format.format(Calendar.getInstance().getTime());
        List<Document> projectList = result.get("projectList");
        excelWriter.set(0, column.incrementAndGet(), "学生名称");
        projectList.forEach(projectDoc -> excelWriter.set(0, column.incrementAndGet(), (projectDoc.getString("startDate") == null ? currentDate : projectDoc.getString("startDate")) + projectDoc.getString("name")));
    }

    private void fillData(ExcelWriter excelWriter, Result result) {
        int row = 1;
        AtomicInteger column = new AtomicInteger(-1);
        List<Map<String, Object>> studentList = result.get("studentList");
        for(Map<String, Object> student : studentList){
            excelWriter.set(row, column.incrementAndGet(), student.get("studentName"));
            List<Map<String, Object>> scores = (List<Map<String, Object>>)student.get("scores");
            for(Map<String, Object> score : scores){
                excelWriter.set(row, column.incrementAndGet(), score.get("score"));
            }
            row++;
            column.set(-1);
        }
    }
}
