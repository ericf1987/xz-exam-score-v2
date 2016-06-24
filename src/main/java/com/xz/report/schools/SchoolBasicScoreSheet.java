package com.xz.report.schools;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.report.Keys;
import com.xz.api.Param;
import com.xz.api.server.school.SchoolScoreAnalysis;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.report.SheetGenerator;
import com.xz.report.SheetTask;
import com.xz.services.ClassService;
import com.xz.services.SchoolService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.xz.ajiaedu.common.report.Keys.ScoreLevel.*;
import static com.xz.util.DoubleUtils.toPercent;

/**
 * @author by fengye on 2016/6/6.
 * 学校成绩分析/基础分析/分数分析
 */
@Component
public class SchoolBasicScoreSheet extends SheetGenerator {
    @Autowired
    SchoolScoreAnalysis schoolScoreAnalysis;

    @Autowired
    ClassService classService;

    @Autowired
    SchoolService schoolService;

    public static String[] COLUMNS_TOTAL = new String[]{
            "班级名称", "实考人数", "最高分", "最低分", "平均分", "标准差",
            "优率", "良率", "合格率", "不及格率", "超均率", "全科及格率", "全科不及格率"
    };

    public static String[] COLUMNS = new String[]{
            "班级名称", "实考人数", "最高分", "最低分", "平均分", "标准差",
            "优率", "良率", "合格率", "不及格率", "超均率"
    };

    public static final String[] RANK_POSITION = new String[]{
            "1/4位", "中位", "3/4位"
    };

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {
        Target target = sheetTask.get("target");
        String subjectId = target.match(Target.PROJECT) ? null : target.getId().toString();

        Range schoolRange = sheetTask.getRange();

        Param param = new Param().setParameter("projectId", projectId).
                setParameter("subjectId", subjectId).
                setParameter("schoolId", schoolRange.getId());

        Result result = schoolScoreAnalysis.execute(param);

        if (null == subjectId) {
            setupHeader(excelWriter, COLUMNS_TOTAL);
            setupSecondaryHeader(excelWriter, COLUMNS_TOTAL);
        } else {
            setupHeader(excelWriter, COLUMNS);
            setupSecondaryHeader(excelWriter, COLUMNS);
        }
        fillSchoolData(result.get("schools"), excelWriter, subjectId);
        fillClassData(result.getList("classes", null), excelWriter, subjectId);
    }

    private void setupHeader(ExcelWriter excelWriter, String[] COLUMNS) {
        AtomicInteger column = new AtomicInteger(-1);
        for (String c : COLUMNS) {
            excelWriter.set(0, column.incrementAndGet(), c);
        }
        excelWriter.set(0, column.incrementAndGet(), "中位数");
        excelWriter.mergeCells(0, column.get(), 0, column.get() + 2);
    }

    private void setupSecondaryHeader(ExcelWriter excelWriter, String[] COLUMNS) {
        AtomicInteger column = new AtomicInteger(-1);
        for (int index = 0; index < COLUMNS.length; index++) {
            excelWriter.set(1, column.incrementAndGet(), COLUMNS[index]);
            excelWriter.mergeCells(0, index, 1, index);
        }
        excelWriter.set(1, column.incrementAndGet(), RANK_POSITION[0]);
        excelWriter.set(1, column.incrementAndGet(), RANK_POSITION[1]);
        excelWriter.set(1, column.incrementAndGet(), RANK_POSITION[2]);
    }

    private void fillSchoolData(Map<String, Object> school, ExcelWriter excelWriter, String subjectId) {
        school.put("className", "总体");
        fillRow(school, excelWriter, 2, subjectId);
    }

    private void fillClassData(List<Map<String, Object>> classes, ExcelWriter excelWriter, String subjectId) {
        int row = 3;
        for (Map<String, Object> classMap : classes) {
            fillRow(classMap, excelWriter, row, subjectId);
            row++;
        }
    }

    private Object getRate(Map<String, Object> rowData, Keys.ScoreLevel scoreLevel) {
        Document document = (Document) rowData.get(scoreLevel.name());
        return document == null ? 0 : toPercent((Double) document.get("rate"));
    }

    /*    "班级名称", "实考人数", "最高分", "最低分", "平均分", "标准差",
                "优率", "良率", "合格率", "不及格率", "全科及格率", "全科不及格率", "中位数"*/
    @SuppressWarnings("unchecked")
    private void fillRow(Map<String, Object> classMap, ExcelWriter excelWriter, int row, String subjectId) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(row, column.incrementAndGet(), classMap.get("className"));
        excelWriter.set(row, column.incrementAndGet(), classMap.get("studentCount"));
        excelWriter.set(row, column.incrementAndGet(), classMap.get("maxScore"));
        excelWriter.set(row, column.incrementAndGet(), classMap.get("minScore"));
        excelWriter.set(row, column.incrementAndGet(), classMap.get("avgScore"));
        excelWriter.set(row, column.incrementAndGet(), classMap.get("stdDeviation"));
        excelWriter.set(row, column.incrementAndGet(), getRate(classMap, Excellent));
        excelWriter.set(row, column.incrementAndGet(), getRate(classMap, Good));
        excelWriter.set(row, column.incrementAndGet(), getRate(classMap, Pass));
        excelWriter.set(row, column.incrementAndGet(), getRate(classMap, Fail));
        excelWriter.set(row, column.incrementAndGet(), toPercent((Double) classMap.get("overAverage")));
        if (null == subjectId) {
            excelWriter.set(row, column.incrementAndGet(), toPercent((Double) classMap.get("allPassRate")));
            excelWriter.set(row, column.incrementAndGet(), toPercent((Double) classMap.get("allFailRate")));
        }
        List<Document> rankPositions = (List<Document>)classMap.get("rankPositions");
        for(Document rankPosition : rankPositions){
            excelWriter.set(row, column.incrementAndGet(), rankPosition.get("score"));
        }
    }
}
