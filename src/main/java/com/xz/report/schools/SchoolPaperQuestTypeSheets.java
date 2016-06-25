package com.xz.report.schools;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.api.server.school.SchoolQuestTypeAnalysis;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.report.SheetGenerator;
import com.xz.report.SheetTask;
import com.xz.services.ClassService;
import com.xz.util.DoubleUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author by fengye on 2016/6/25.
 */
@SuppressWarnings("unchecked")
@Component
public class SchoolPaperQuestTypeSheets extends SheetGenerator {

    @Autowired
    SchoolQuestTypeAnalysis schoolQuestTypeAnalysis;

    @Autowired
    ClassService classService;

    public static final String[] SECONDARY_COLUMN = new String[]{
            "分数", "得分率"
    };

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {
        Target target = sheetTask.get("target");
        String subjectId = target.getId().toString();
        Range schoolRange = sheetTask.getRange();
        Param param = new Param().setParameter("projectId", projectId).
                setParameter("schoolId", schoolRange.getId()).setParameter("subjectId", subjectId);
        Result result = schoolQuestTypeAnalysis.execute(param);
        System.out.println("学校试卷分析-->" + result.getData());
        setupHeader(excelWriter, result);
        setupSecondaryHeader(excelWriter, result);
        fillSchoolData(excelWriter, result);
        fillClassData(excelWriter, result);
    }

    private void setupHeader(ExcelWriter excelWriter, Result result) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(0, column.incrementAndGet(), "题型");
        List<Map<String, Object>> schools = result.get("schools");
        for (Map<String, Object> school : schools) {
            excelWriter.set(0, column.incrementAndGet(), school.get("name").toString());
            excelWriter.mergeCells(0, column.get(), 0, column.incrementAndGet());
        }
    }

    private void setupSecondaryHeader(ExcelWriter excelWriter, Result result) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(1, column.incrementAndGet(), "题型");
        excelWriter.mergeCells(0, column.get(), 1, column.get());
        List<Map<String, Object>> schools = result.get("schools");
        for (int i = 0; i < schools.size(); i++) {
            excelWriter.set(1, column.incrementAndGet(), SECONDARY_COLUMN[0]);
            excelWriter.set(1, column.incrementAndGet(), SECONDARY_COLUMN[1]);
        }
    }

    private void fillSchoolData(ExcelWriter excelWriter, Result result) {
        int row = 2;
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(row, column.incrementAndGet(), "本校");
        List<Map<String, Object>> schools = result.get("schools");
        for (Map<String, Object> school : schools) {
            excelWriter.set(row, column.incrementAndGet(), school.get("score"));
            excelWriter.set(row, column.incrementAndGet(), DoubleUtils.toPercent(Double.parseDouble(school.get("scoreRate").toString())));
        }
    }

    private void fillClassData(ExcelWriter excelWriter, Result result) {
        int row = 3;
        AtomicInteger column = new AtomicInteger(-1);
        List<Map<String, Object>> classes = result.get("classes");
        for (Map<String, Object> clazz : classes) {
            excelWriter.set(row, column.incrementAndGet(), clazz.get("className"));
            List<Map<String, Object>> questTypes = (List<Map<String, Object>>)clazz.get("questTypes");
            for(Map<String, Object> questType : questTypes){
                excelWriter.set(row, column.incrementAndGet(), questType.get("score"));
                excelWriter.set(row, column.incrementAndGet(), DoubleUtils.toPercent(Double.parseDouble(questType.get("scoreRate").toString())));
            }
            row++;
            column.set(-1);
        }
    }
}
