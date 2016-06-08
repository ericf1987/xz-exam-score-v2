package com.xz.report.total;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.api.server.project.ProjectRankStat;
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
import java.util.stream.Collectors;

/**
 * @author by fengye on 2016/6/3.
 */
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

        //设置表头
        setupHeader(excelWriter);
        fillSchoolData(result.getList("schools", null), excelWriter);
    }

    private void fillSchoolData(List<Map<String, Object>> school, ExcelWriter excelWriter) {
        int row = 1;
        for (Map<String, Object> schoolMap : school) {
            fillRow(schoolMap, excelWriter, row);
            row++;
        }
    }

    private void fillRow(Map<String, Object> ranks, ExcelWriter excelWriter, int rowIndex) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(rowIndex, column.incrementAndGet(), ranks.get("schoolName"));
        excelWriter.set(rowIndex, column.incrementAndGet(), ranks.get("studentCount"));
        List<Map<String, Object>> rankStat = (List<Map<String, Object>>) ranks.get("rankStat");
        for (Map<String, Object> r : rankStat) {
            excelWriter.set(rowIndex, column.incrementAndGet(), r.get("count"));
        }
    }

    private void setupHeader(ExcelWriter excelWriter) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(0, column.incrementAndGet(), "学校名称");
        excelWriter.set(0, column.incrementAndGet(), "实考人数");
        for (double d : PIECE_WISE) {
            excelWriter.set(0, column.incrementAndGet(), "总排名前" + DoubleUtils.toPercent(d));
        }
    }
}
