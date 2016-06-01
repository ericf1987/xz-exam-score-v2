package com.xz.report.total;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.report.Keys.ScoreLevel;
import com.xz.api.Param;
import com.xz.api.server.project.ProjectScoreAnalysis;
import com.xz.bean.Target;
import com.xz.report.SheetGenerator;
import com.xz.report.SheetTask;
import com.xz.services.SchoolService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.xz.ajiaedu.common.lang.NumberUtil.toPercent;
import static com.xz.ajiaedu.common.report.Keys.ScoreLevel.*;

/**
 * (description)
 * created at 16/05/31
 *
 * @author yiding_he
 */
@Component
public class TotalBasicScoreSheet extends SheetGenerator {

    @Autowired
    ProjectScoreAnalysis projectScoreAnalysis;

    @Autowired
    SchoolService schoolService;

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {
        Target target = sheetTask.get("target");
        String subjectId = target.match(Target.PROJECT) ? null : target.getId().toString();

        List<String> schoolIds = schoolService.getProjectSchools(projectId)
                .stream().map(d -> d.getString("school")).collect(Collectors.toList());

        Param param = new Param()
                .setParameter("projectId", projectId)
                .setParameter("subjectId", subjectId)
                .setParameter("schoolIds", schoolIds.toArray(new String[schoolIds.size()]));

        Result result = projectScoreAnalysis.execute(param);

        setupHeader(excelWriter);
        fillProvinceData(result.get("totals"), excelWriter);
        fillSchoolData(result.getList("schools", null), excelWriter);
    }

    // 填充学校数据
    private void fillSchoolData(List<Map<String, Object>> schoolStat, ExcelWriter excelWriter) {
        int row = 2;
        for (Map<String, Object> schoolMap : schoolStat) {
            fillRow(schoolMap, excelWriter, row);
            row++;
        }
    }

    // 填充整体数据
    private void fillProvinceData(Map<String, Object> totalStat, ExcelWriter excelWriter) {
        totalStat.put("schoolName", "总体");
        fillRow(totalStat, excelWriter, 1);

    }

    private void fillRow(Map<String, Object> rowData, ExcelWriter excelWriter, int rowindex) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(rowindex, column.incrementAndGet(), rowData.get("schoolName"));
        excelWriter.set(rowindex, column.incrementAndGet(), rowData.get("studentCount"));
        excelWriter.set(rowindex, column.incrementAndGet(), rowData.get("maxScore"));
        excelWriter.set(rowindex, column.incrementAndGet(), rowData.get("minScore"));
        excelWriter.set(rowindex, column.incrementAndGet(), rowData.get("avgScore"));
        excelWriter.set(rowindex, column.incrementAndGet(), rowData.get("stdDeviation"));
        excelWriter.set(rowindex, column.incrementAndGet(), getRate(rowData, Excellent));
        excelWriter.set(rowindex, column.incrementAndGet(), getRate(rowData, Good));
        excelWriter.set(rowindex, column.incrementAndGet(), getRate(rowData, Pass));
        excelWriter.set(rowindex, column.incrementAndGet(), getRate(rowData, Fail));
        excelWriter.set(rowindex, column.incrementAndGet(), toPercent((Double) rowData.get("allPassRate")));
        excelWriter.set(rowindex, column.incrementAndGet(), toPercent((Double) rowData.get("allFailRate")));
    }

    private Object getRate(Map<String, Object> rowData, ScoreLevel scoreLevel) {
        Document document = (Document) rowData.get(scoreLevel.name());
        return document == null ? 0 : toPercent((Double) document.get("rate"));
    }

    // 填充表头
    private void setupHeader(ExcelWriter excelWriter) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(0, column.incrementAndGet(), "学校名称");
        excelWriter.set(0, column.incrementAndGet(), "实考人数");
        excelWriter.set(0, column.incrementAndGet(), "最高分");
        excelWriter.set(0, column.incrementAndGet(), "最低分");
        excelWriter.set(0, column.incrementAndGet(), "平均分");
        excelWriter.set(0, column.incrementAndGet(), "标准差");
        excelWriter.set(0, column.incrementAndGet(), "优率");
        excelWriter.set(0, column.incrementAndGet(), "良率");
        excelWriter.set(0, column.incrementAndGet(), "及格率");
        excelWriter.set(0, column.incrementAndGet(), "不及格率");
        excelWriter.set(0, column.incrementAndGet(), "全科及格率");
        excelWriter.set(0, column.incrementAndGet(), "全科不及格率");
    }
}
