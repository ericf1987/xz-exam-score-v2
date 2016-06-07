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

import static com.xz.ajiaedu.common.lang.NumberUtil.toPercent;
import static com.xz.ajiaedu.common.report.Keys.ScoreLevel.*;

/**
 * @author by fengye on 2016/6/6.
 */
@Component
public class SchoolBasicScoreSheet extends SheetGenerator {
    @Autowired
    SchoolScoreAnalysis schoolScoreAnalysis;

    @Autowired
    ClassService classService;

    @Autowired
    SchoolService schoolService;

    public static String[] COLUMNS = new String[]{
        "班级名称", "实考人数", "最高分", "最低分", "平均分", "标准差",
        "优率", "良率", "合格率", "不及格率", "全科及格率", "全科不及格率", "中位数"
    };

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {
        Target target = sheetTask.get("target");
        String subjectId = target.match(Target.PROJECT) ? null : target.getId().toString();

        Range schoolRange = sheetTask.getRange();
/*        List<String> classIds = classService.listClasses(projectId, schoolRange.getId().toString()).
                stream().map(d -> d.getString("class")).collect(Collectors.toList());*/

        Param param = new Param().setParameter("projectId", projectId).
                setParameter("subjectId", subjectId).
                setParameter("schoolIds", schoolRange.getId().toString());

        Result result = schoolScoreAnalysis.execute(param);

        //System.out.println("学校分数分析-->" + result.getData());
        setupHeader(excelWriter);
        fillSchoolData(result.get("schools"),excelWriter);
        fillClassData(result.getList("classes", null),excelWriter);
    }

    private void setupHeader(ExcelWriter excelWriter) {
        AtomicInteger column = new AtomicInteger(-1);
        for(String c : COLUMNS){
            excelWriter.set(0, column.incrementAndGet(), c);
        }
    }

    private void fillSchoolData(Map<String, Object> school, ExcelWriter excelWriter) {
        school.put("className", "总体");
        fillRow(school, excelWriter, 1);
    }

    private void fillClassData(List<Map<String, Object>> classes, ExcelWriter excelWriter) {
        int row = 2;
        for(Map<String, Object> classMap : classes){
            fillRow(classMap, excelWriter, row);
            row++;
        }
    }

    private Object getRate(Map<String, Object> rowData, Keys.ScoreLevel scoreLevel) {
        Document document = (Document) rowData.get(scoreLevel.name());
        return document == null ? 0 : toPercent((Double) document.get("rate"));
    }

/*    "班级名称", "实考人数", "最高分", "最低分", "平均分", "标准差",
            "优率", "良率", "合格率", "不及格率", "全科及格率", "全科不及格率", "中位数"*/
    private void fillRow(Map<String, Object> classMap, ExcelWriter excelWriter, int row) {
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
        excelWriter.set(row, column.incrementAndGet(), toPercent((Double)classMap.get("allPassRate")));
        excelWriter.set(row, column.incrementAndGet(), toPercent((Double)classMap.get("allFailRate")));
    }
}
