package com.xz.examscore.asynccomponents.report.total;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.server.project.ProjectRankStat;
import com.xz.examscore.asynccomponents.report.SheetGenerator;
import com.xz.examscore.asynccomponents.report.SheetTask;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.SchoolService;
import com.xz.examscore.util.DoubleUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author by fengye on 2016/6/3.
 * 总体成绩分析-基础分析-排名统计
 */
@SuppressWarnings("unchecked")
@Component
public class TotalBasicRankSheet extends SheetGenerator {

    @Autowired
    ProjectRankStat projectRankStat;

    @Autowired
    SchoolService schoolService;

    //分段
    public static final double[] PIECE_WISE = new double[]{
            0.05, 0.1, 0.15, 0.2,
            0.25, 0.3, 0.35, 0.4,
            0.45, 0.5, 0.55, 0.6,
            0.65, 0.7, 0.75, 0.8,
            0.85, 0.9, 0.95, 1.0
    };

    public static final String[] SECONDARY_HEADER = new String[]{
            "人数", "占比", "累计"
    };

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {
        Target target = sheetTask.getTarget();
        String subjectId = target.match(Target.PROJECT) ? null : target.getId().toString();

        //获取所有联考学校的ID
        List<String> schoolIds = schoolService.getProjectSchools(projectId).
                stream().map(d -> d.getString("school")).collect(Collectors.toList());

        Param param = new Param().setParameter("projectId", projectId)
                .setParameter("subjectId", subjectId)
                .setParameter("schoolIds", schoolIds.toArray(new String[schoolIds.size()]));

        Result result = projectRankStat.execute(param);

        System.out.println(result.getData());
        //设置表头
        setupHeader(excelWriter);
        setupSecondaryHeader(excelWriter);
        fillSchoolData(result.getList("schools", null), excelWriter);
    }

    private void fillSchoolData(List<Map<String, Object>> school, ExcelWriter excelWriter) {
        int row = 2;
        for (Map<String, Object> schoolMap : school) {
            fillRow(schoolMap, excelWriter, row);
            row++;
        }
    }

    private void fillRow(Map<String, Object> ranks, ExcelWriter excelWriter, int rowIndex) {
        AtomicInteger column = new AtomicInteger(-1);
        int studentCount = (int) ranks.get("studentCount");
        excelWriter.set(rowIndex, column.incrementAndGet(), ranks.get("schoolName"));
        excelWriter.set(rowIndex, column.incrementAndGet(), studentCount);
        List<Map<String, Object>> rankStat = (List<Map<String, Object>>) ranks.get("rankStat");
        int accCount = 0;
        for (Map<String, Object> r : rankStat) {
            excelWriter.set(rowIndex, column.incrementAndGet(), r.get("count"));
            excelWriter.set(rowIndex, column.incrementAndGet(), DoubleUtils.toPercent((double) r.get("rate")));
            int count = (int) r.get("count");
            accCount += count;
            double accRate = (double) accCount / (double) studentCount;
            excelWriter.set(rowIndex, column.incrementAndGet(), DoubleUtils.toPercent(accRate));
        }
    }

    private void setupHeader(ExcelWriter excelWriter) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(0, column.incrementAndGet(), "学校名称");
        excelWriter.set(0, column.incrementAndGet(), "实考人数");
        for (double d : PIECE_WISE) {
            excelWriter.set(0, column.incrementAndGet(), "总排名前" + DoubleUtils.toPercent(d));
            column.incrementAndGet();
            column.incrementAndGet();
            excelWriter.mergeCells(0, column.get() - 2, 0, column.get());
        }
    }

    private void setupSecondaryHeader(ExcelWriter excelWriter) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(1, column.incrementAndGet(), "学校名称");
        excelWriter.set(1, column.incrementAndGet(), "实考人数");
        excelWriter.mergeCells(0, 0, 1, 0);
        excelWriter.mergeCells(0, 1, 1, 1);
        for (int i = 0; i < PIECE_WISE.length; i++) {
            excelWriter.set(1, column.incrementAndGet(), SECONDARY_HEADER[0]);
            excelWriter.set(1, column.incrementAndGet(), SECONDARY_HEADER[1]);
            excelWriter.set(1, column.incrementAndGet(), SECONDARY_HEADER[2]);
        }
    }
}
