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

import static com.xz.ajiaedu.common.report.Keys.ScoreLevel.*;
import static com.xz.util.DoubleUtils.toPercent;

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

    public static final String[] HEADER = new String[]{
            "学校名称", "实考人数", "最高分", "最低分", "平均分",
            "标准差", "优率", "良率", "及格率", "不及格率",
            "超均率"
    };

    public static final String[] RANK_POSITION = new String[]{
            "1/4位", "中位", "3/4位"
    };

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

        setupHeader(excelWriter, subjectId);
        setupSecondaryHeader(excelWriter, subjectId);
        fillProvinceData(result.get("totals"), excelWriter, subjectId);
        fillSchoolData(result.getList("schools", null), excelWriter, subjectId);
    }

    // 填充学校数据
    private void fillSchoolData(List<Map<String, Object>> schoolStat, ExcelWriter excelWriter, String subjectId) {
        int row = 3;
        for (Map<String, Object> schoolMap : schoolStat) {
            fillRow(schoolMap, excelWriter, row, subjectId);
            row++;
        }
    }

    // 填充整体数据
    private void fillProvinceData(Map<String, Object> totalStat, ExcelWriter excelWriter, String subjectId) {
        totalStat.put("schoolName", "总体");
        fillRow(totalStat, excelWriter, 2, subjectId);

    }

    @SuppressWarnings("unchecked")
    private void fillRow(Map<String, Object> rowData, ExcelWriter excelWriter, int rowindex, String subjectId) {
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
        excelWriter.set(rowindex, column.incrementAndGet(), toPercent((Double) rowData.get("overAverage")));
        if (null == subjectId) {
            excelWriter.set(rowindex, column.incrementAndGet(), toPercent((Double) rowData.get("allPassRate")));
            excelWriter.set(rowindex, column.incrementAndGet(), toPercent((Double) rowData.get("allFailRate")));
        }
        List<Document> rankPositions = (List<Document>) rowData.get("rankPositions");
        for (Document rankPosition : rankPositions) {
            excelWriter.set(rowindex, column.incrementAndGet(), rankPosition.get("score"));
        }

    }

    private Object getRate(Map<String, Object> rowData, ScoreLevel scoreLevel) {
        Document document = (Document) rowData.get(scoreLevel.name());
        return document == null ? 0 : toPercent((Double) document.get("rate"));
    }

    // 填充表头
    private void setupHeader(ExcelWriter excelWriter, String subjectId) {
        AtomicInteger column = new AtomicInteger(-1);
        for (String s : HEADER) {
            excelWriter.set(0, column.incrementAndGet(), s);
        }
        if (null == subjectId) {
            excelWriter.set(0, column.incrementAndGet(), "全科及格率");
            excelWriter.set(0, column.incrementAndGet(), "全科不及格率");
        }
        excelWriter.set(0, column.incrementAndGet(), "中位数");
        excelWriter.mergeCells(0, column.get(), 0, column.get() + 2);
    }

    private void setupSecondaryHeader(ExcelWriter excelWriter, String subjectId) {
        AtomicInteger column = new AtomicInteger(-1);
        for (int index = 0; index < HEADER.length; index++) {
            excelWriter.set(1, column.incrementAndGet(), HEADER[index]);
            excelWriter.mergeCells(0, index, 1, index);
        }
        if (null == subjectId) {
            excelWriter.set(1, column.incrementAndGet(), "全科及格率");
            excelWriter.set(1, column.incrementAndGet(), "全科不及格率");
            excelWriter.mergeCells(0, column.get() - 1, 1, column.get() - 1);
            excelWriter.mergeCells(0, column.get(), 1, column.get());
        }
        excelWriter.set(1, column.incrementAndGet(), RANK_POSITION[0]);
        excelWriter.set(1, column.incrementAndGet(), RANK_POSITION[1]);
        excelWriter.set(1, column.incrementAndGet(), RANK_POSITION[2]);
    }

}
