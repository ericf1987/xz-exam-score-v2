package com.xz.report.schools;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.api.server.school.SchoolSubjectAnalysis;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.report.SheetGenerator;
import com.xz.report.SheetTask;
import com.xz.services.SchoolService;
import com.xz.util.DoubleUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author by fengye on 2016/6/7.
 */
@Component
public class SchoolBasicSubjectSheet extends SheetGenerator {
    @Autowired
    SchoolSubjectAnalysis schoolSubjectAnalysis;

    @Autowired
    SchoolService schoolService;

    public static final String[] SECONDARY_HEADER = new String[]{
            "平均分", "贡献度", "T分值"
    };

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {
        Target target = sheetTask.get("target");
        String subjectId = target.match(Target.PROJECT) ? null : target.getId().toString();
        Range schoolRanges = sheetTask.getRange();

        Param param = new Param().setParameter("projectId", projectId)
                .setParameter("subjectId", subjectId)
                .setParameter("schoolId", schoolRanges.getId());

        Result result = schoolSubjectAnalysis.execute(param);

        //设置表头
        setupHeader(excelWriter, result.get("schools"));
        setupSecondaryHeader(excelWriter, result.get("schools"));
        fillSchoolData(result.get("schools"), excelWriter);
        fillClassData(result.get("classes"), excelWriter);
    }

    private void fillClassData(List<Map<String, Object>> classes, ExcelWriter excelWriter) {
        int row = 3;
        for (Map<String, Object> clazz : classes) {
            fillRow(clazz, excelWriter, row);
            row++;
        }
    }

    private void fillSchoolData(Map<String, Object> school, ExcelWriter excelWriter) {
        //将汇总信息填充至第一行
        fillRow(school, excelWriter, 2);
    }

    private void fillRow(Map<String, Object> clazz, ExcelWriter excelWriter, int row) {
        AtomicInteger column = new AtomicInteger(-1);
        if (row == 2) {
            excelWriter.set(row, column.incrementAndGet(), "全校");
        } else {
            excelWriter.set(row, column.incrementAndGet(), clazz.get("className"));
        }
        excelWriter.set(row, column.incrementAndGet(), clazz.get("studentCount"));
        excelWriter.set(row, column.incrementAndGet(), clazz.get("totalAvg"));
        List<Map<String, Object>> subjects = (List<Map<String, Object>>) clazz.get("subjects");
        for (Map<String, Object> subject : subjects) {
            excelWriter.set(row, column.incrementAndGet(), subject.get("subjectAvg"));
            excelWriter.set(row, column.incrementAndGet(), DoubleUtils.toPercent((Double) (subject.get("subjectRate"))));
            excelWriter.set(row, column.incrementAndGet(), subject.get("tScore"));
        }
    }

    private void setupHeader(ExcelWriter excelWriter, Map<String, Object> para) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(0, column.incrementAndGet(), "班级名称");
        excelWriter.set(0, column.incrementAndGet(), "实考人数");
        excelWriter.set(0, column.incrementAndGet(), "总分平均分");
        List<Map<String, Object>> subjects = (List<Map<String, Object>>) para.get("subjects");
        for (Map subject : subjects) {
            excelWriter.set(0, column.incrementAndGet(), subject.get("subjectName").toString());
            column.incrementAndGet();
            column.incrementAndGet();
            excelWriter.mergeCells(0, column.get() - 2, 0, column.get());
        }
    }

    private void setupSecondaryHeader(ExcelWriter excelWriter, Map<String, Object> schools) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(1, column.incrementAndGet(), "班级名称");
        excelWriter.set(1, column.incrementAndGet(), "实考人数");
        excelWriter.set(1, column.incrementAndGet(), "总分平均分");
        excelWriter.mergeCells(0, 0, 1, 0);
        excelWriter.mergeCells(0, 1, 1, 1);
        excelWriter.mergeCells(0, 2, 1, 2);
        List<Map<String, Object>> subjects = (List<Map<String, Object>>) schools.get("subjects");
        for (Map<String, Object> subject : subjects) {
            excelWriter.set(1, column.incrementAndGet(), SECONDARY_HEADER[0]);
            excelWriter.set(1, column.incrementAndGet(), SECONDARY_HEADER[1]);
            excelWriter.set(1, column.incrementAndGet(), SECONDARY_HEADER[2]);
        }
    }
}
