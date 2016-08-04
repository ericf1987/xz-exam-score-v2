package com.xz.report.schools.compare;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.api.server.school.compare.SchoolPassCompareAnalysis;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.report.SheetGenerator;
import com.xz.report.SheetTask;
import com.xz.util.DoubleUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author by fengye on 2016/7/26.
 * 学校成绩分析-历次考试对比-及格率对比
 */
@SuppressWarnings("unchecked")
@Component
public class SchoolPassCompareSheets extends SheetGenerator {

    @Autowired
    SchoolAverageCompareSheets schoolAverageCompareSheets;

    @Autowired
    SchoolPassCompareAnalysis schoolPassCompareAnalysis;

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {
        Target target = sheetTask.get("target");
        String subjectId = target.match(Target.PROJECT) ? null : target.getId().toString();
        Range schoolRange = sheetTask.getRange();
        Param param = new Param().setParameter("projectId", projectId).
                setParameter("subjectId", subjectId).
                setParameter("schoolId", schoolRange.getId());

        Result result = schoolPassCompareAnalysis.execute(param);

        schoolAverageCompareSheets.setHeader(excelWriter, result);
        fillSchoolData(excelWriter, result);
        fillClassData(excelWriter, result);
    }

    private void fillSchoolData(ExcelWriter excelWriter, Result result) {
        AtomicInteger column = new AtomicInteger(-1);
        Map<String, Object> school = result.get("school");
        excelWriter.set(1, column.incrementAndGet(), "本校");
        List<Map<String, Object>> averages = (List<Map<String, Object>>) school.get("passes");
        averages.forEach(average -> excelWriter.set(1, column.incrementAndGet(), DoubleUtils.toPercent((Double)average.get("rate"))));
    }

    private void fillClassData(ExcelWriter excelWriter, Result result) {
        int row = 2;
        AtomicInteger column = new AtomicInteger(-1);
        List<Map<String, Object>> classes = result.get("classes");
        for (Map<String, Object> clazz : classes) {
            excelWriter.set(row, column.incrementAndGet(), clazz.get("className"));
            List<Map<String, Object>> averages = (List<Map<String, Object>>) clazz.get("passes");
            for (Map<String, Object> average : averages) {
                excelWriter.set(row, column.incrementAndGet(), DoubleUtils.toPercent((Double)average.get("rate")));
            }
            row++;
            column.set(-1);
        }
    }
}
