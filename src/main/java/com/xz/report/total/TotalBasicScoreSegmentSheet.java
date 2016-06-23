package com.xz.report.total;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.api.server.project.ProjectScoreSegment;
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

import static com.xz.util.DoubleUtils.toPercent;

/**
 * @author by fengye on 2016/6/6.
 */
@Component
public class TotalBasicScoreSegmentSheet extends SheetGenerator {
    @Autowired
    ProjectScoreSegment projectScoreSegment;

    @Autowired
    SchoolService schoolService;

    public static final String[] SECONDARY_HEADER = new String[]{
            "人数","占比"
    };

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {
        Target target = sheetTask.get("target");
        String subjectId = target.match(Target.PROJECT) ? null : target.getId().toString();

        List<String> schoolIds = schoolService.getProjectSchools(projectId).stream().
                map(d -> d.getString("school")).collect(Collectors.toList());

        Param param = new Param().setParameter("projectId", projectId).
                setParameter("subjectId", subjectId).
                setParameter("schoolIds", schoolIds.toArray(new String[schoolIds.size()]));

        Result result = projectScoreSegment.execute(param);
        setupHeader(excelWriter, result.get("totals"));
        setupSecondaryHeader(excelWriter, result.get("totals"));
        fillProviceData(result.get("totals"), excelWriter);
        fillSchoolData(result.getList("schools", null), excelWriter);
    }

    private void setupHeader(ExcelWriter excelWriter, List<Map<String, Object>> list) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(0, column.incrementAndGet(), "学校名称");
        for(Map<String, Object> m : list){
            excelWriter.set(0, column.incrementAndGet(), m.get("title"));
            column.incrementAndGet();
            excelWriter.mergeCells(0, column.get() - 1, 0, column.get());
        }
    }

    private void setupSecondaryHeader(ExcelWriter excelWriter, List<Map<String, Object>> schools){
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(1, column.incrementAndGet(), "学校名称");
        excelWriter.mergeCells(0, 0, 1, 0);

        for (int i = 0; i < schools.size(); i++) {
            excelWriter.set(1, column.incrementAndGet(), SECONDARY_HEADER[0]);
            excelWriter.set(1, column.incrementAndGet(), SECONDARY_HEADER[1]);
        }
    }

    //填充报表数据
    private void fillSchoolData(List<Map<String, Object>> schools, ExcelWriter excelWriter) {
        int row = 3;
        for(Map<String, Object> m : schools){
            fillRow(m, excelWriter, row);
            row++;
        }
    }

    //填充汇总数据
    private void fillProviceData(List<Map<String, Object>> totals, ExcelWriter excelWriter) {
        int row = 2;
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(row, column.incrementAndGet(), "总体");
        for(Map<String, Object> total : totals){
            excelWriter.set(row, column.incrementAndGet(), total.get("count"));
            excelWriter.set(row, column.incrementAndGet(), toPercent((double)total.get("countRate")));
        }
    }

    @SuppressWarnings("unchecked")
    private void fillRow(Map<String, Object> school, ExcelWriter excelWriter, int row) {
        AtomicInteger column = new AtomicInteger(-1);
        String schoolName = school.get("schoolName").toString();
        List<Map<String, Object>> scoreSegment = (List<Map<String, Object>>)school.get("scoreSegments");
        excelWriter.set(row, column.incrementAndGet(), schoolName);
        for(Map<String, Object> item : scoreSegment){
            excelWriter.set(row, column.incrementAndGet(), item.get("count"));
            excelWriter.set(row, column.incrementAndGet(), toPercent((double)item.get("countRate")));
        }
    }

}
