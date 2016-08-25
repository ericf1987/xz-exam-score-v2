package com.xz.examscore.asynccomponents.report.schools;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.ajiaedu.common.excel.HAlign;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.server.school.SchoolScoreSegment;
import com.xz.examscore.asynccomponents.report.SheetGenerator;
import com.xz.examscore.asynccomponents.report.SheetTask;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.SchoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.xz.examscore.util.DoubleUtils.toPercent;

/**
 * @author by fengye on 2016/6/7.
 * 学校成绩分析-基础分析-分数段统计
 */
@Component
public class SchoolBasicScoreSegmentSheet extends SheetGenerator {

    @Autowired
    SchoolScoreSegment schoolScoreSegment;

    @Autowired
    SchoolService schoolService;

    public static final String[] SECONDARY_HEADER = new String[]{
            "人数", "占比"
    };

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {
        Target target = sheetTask.get("target");
        String subjectId = target.match(Target.PROJECT) ? null : target.getId().toString();
        Range schoolRange = sheetTask.getRange();
        Param param = new Param().setParameter("projectId", projectId).
                setParameter("subjectId", subjectId).
                setParameter("schoolId", schoolRange.getId());

        Result result = schoolScoreSegment.execute(param);

        setupHeader(excelWriter, result.get("schools"));
        setupSecondaryHeader(excelWriter, result.get("schools"));
        fillSchoolData(result.get("schools"), excelWriter);
        fillClassData(result.getList("classes", null), excelWriter);
    }

    private void setupHeader(ExcelWriter excelWriter, List<Map<String, Object>> schools) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(0, column.incrementAndGet(), "班级名称");
        for (Map<String, Object> school : schools) {
            excelWriter.set(0, column.incrementAndGet(), school.get("title"));
            column.incrementAndGet();
            excelWriter.mergeCells(0, column.get() - 1, 0, column.get());
            excelWriter.setCellHorizontalAlign(0, column.get() - 1, HAlign.Center);
        }
    }

    private void setupSecondaryHeader(ExcelWriter excelWriter, List<Map<String, Object>> schools) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(1, column.incrementAndGet(), "班级名称");
        excelWriter.mergeCells(0, 0, 1, 0);
        for (int i = 0; i < schools.size(); i++) {
            excelWriter.set(1, column.incrementAndGet(), SECONDARY_HEADER[0]);
            excelWriter.set(1, column.incrementAndGet(), SECONDARY_HEADER[1]);
        }
    }

    private void fillSchoolData(List<Map<String, Object>> schools, ExcelWriter excelWriter) {
        int row = 2;
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(row, column.incrementAndGet(), "总数");
        for (Map<String, Object> school : schools) {
            excelWriter.set(row, column.incrementAndGet(), school.get("count"));
            excelWriter.set(row, column.incrementAndGet(), toPercent((double) school.get("countRate")));
        }
    }

    private void fillClassData(List<Map<String, Object>> classes, ExcelWriter excelWriter) {
        int row = 3;
        for (Map<String, Object> clazz : classes) {
            fillRows(clazz, excelWriter, row);
            row++;
        }
    }

    @SuppressWarnings("unchecked")
    private void fillRows(Map<String, Object> clazz, ExcelWriter excelWriter, int row) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(row, column.incrementAndGet(), clazz.get("className"));
        for (Map<String, Object> scoreSegment : (List<Map<String, Object>>) clazz.get("scoreSegments")) {
            excelWriter.set(row, column.incrementAndGet(), scoreSegment.get("count"));
            excelWriter.set(row, column.incrementAndGet(), toPercent((double) scoreSegment.get("countRate")));
        }
    }


}
