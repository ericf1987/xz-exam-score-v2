package com.xz.report.schools.compare;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.api.server.school.compare.SchoolAverageCompareAnalysis;
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
 * 学校成绩分析-历次考试对比-平均分对比
 */
@SuppressWarnings("unchecked")
@Component
public class SchoolAverageCompareSheets extends SheetGenerator {

    @Autowired
    SchoolAverageCompareAnalysis schoolAverageCompareAnalysis;

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {
        Target target = sheetTask.get("target");
        String subjectId = target.match(Target.PROJECT) ? null : target.getId().toString();
        Range schoolRange = sheetTask.getRange();
        Param param = new Param().setParameter("projectId", projectId).
                setParameter("subjectId", subjectId).
                setParameter("schoolId", schoolRange.getId());

        Result result = schoolAverageCompareAnalysis.execute(param);

        setHeader(excelWriter, result);
        fillSchoolData(excelWriter, result);
        fillClassData(excelWriter, result);
    }

    public void setHeader(ExcelWriter excelWriter, Result result) {
        AtomicInteger column = new AtomicInteger(-1);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = format.format(Calendar.getInstance().getTime());
        List<Document> projectList = result.get("projectList");
        excelWriter.set(0, column.incrementAndGet(), "班级名称");
        projectList.forEach(projectDoc -> excelWriter.set(0, column.incrementAndGet(), (projectDoc.getString("startDate") == null ? currentDate : projectDoc.getString("startDate")) + projectDoc.getString("name")));
    }

    private void fillSchoolData(ExcelWriter excelWriter, Result result) {
        AtomicInteger column = new AtomicInteger(-1);
        Map<String, Object> school = result.get("school");
        excelWriter.set(1, column.incrementAndGet(), "本校");
        List<Map<String, Object>> averages = (List<Map<String, Object>>) school.get("averages");
        averages.forEach(average -> excelWriter.set(1, column.incrementAndGet(), average.get("score")));
    }

    private void fillClassData(ExcelWriter excelWriter, Result result) {
        int row = 2;
        AtomicInteger column = new AtomicInteger(-1);
        List<Map<String, Object>> classes = result.get("classes");
        for (Map<String, Object> clazz : classes) {
            excelWriter.set(row, column.incrementAndGet(), clazz.get("className"));
            List<Map<String, Object>> averages = (List<Map<String, Object>>) clazz.get("averages");
            for (Map<String, Object> average : averages) {
                excelWriter.set(row, column.incrementAndGet(), average.get("score"));
            }
            row++;
            column.set(-1);
        }
    }
}
