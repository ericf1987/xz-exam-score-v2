package com.xz.report.total;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.api.server.project.ProjectRankStat;
import com.xz.bean.Target;
import com.xz.report.SheetGenerator;
import com.xz.report.SheetTask;
import com.xz.services.SchoolService;
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

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {
        Target target = sheetTask.get("target");
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
        excelWriter.set(0, column.incrementAndGet(), "总排名前5%");
        excelWriter.set(0, column.incrementAndGet(), "总排名前10%");
        excelWriter.set(0, column.incrementAndGet(), "总排名前15%");
        excelWriter.set(0, column.incrementAndGet(), "总排名前20%");
        excelWriter.set(0, column.incrementAndGet(), "总排名前25%");
        excelWriter.set(0, column.incrementAndGet(), "总排名前35%");
        excelWriter.set(0, column.incrementAndGet(), "总排名前40%");
        excelWriter.set(0, column.incrementAndGet(), "总排名前45%");
        excelWriter.set(0, column.incrementAndGet(), "总排名前50%");
        excelWriter.set(0, column.incrementAndGet(), "总排名前55%");
        excelWriter.set(0, column.incrementAndGet(), "总排名前60%");
        excelWriter.set(0, column.incrementAndGet(), "总排名前65%");
        excelWriter.set(0, column.incrementAndGet(), "总排名前70%");
        excelWriter.set(0, column.incrementAndGet(), "总排名前75%");
        excelWriter.set(0, column.incrementAndGet(), "总排名前80%");
        excelWriter.set(0, column.incrementAndGet(), "总排名前85%");
        excelWriter.set(0, column.incrementAndGet(), "总排名前90%");
        excelWriter.set(0, column.incrementAndGet(), "总排名前95%");
    }
}
