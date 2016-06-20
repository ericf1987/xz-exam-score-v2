package com.xz.report.total;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.api.server.project.ProjectHighSegmentAnalysis;
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
 * @author by fengye on 2016/6/17.
 */
@Component
public class TotalTopStudentHScoreSheets extends SheetGenerator {
    @Autowired
    ProjectHighSegmentAnalysis projectHighSegmentAnalysis;

    @Autowired
    SchoolService schoolService;

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {
        List<String> schoolIds = schoolService.getProjectSchools(projectId).stream().
                map(d -> d.getString("school")).collect(Collectors.toList());
        Param param = new Param().setParameter("projectId", projectId).setParameter("percent", 0.3d)
                .setParameter("schoolIds", schoolIds.toArray(new String[schoolIds.size()]));
        Result result = projectHighSegmentAnalysis.execute(param);
        //System.out.println("高分统计-->" + result.getData());
        setupHeader(excelWriter, result.get("totals"));
        fillTotalData(excelWriter, result.get("totals"));
        fillData(excelWriter, result.get("schools"));
    }

    private void setupHeader(ExcelWriter excelWriter, List<Map<String, Object>> totals) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(0, column.incrementAndGet(), "学校名称");
        for(Map<String, Object> total : totals){
            excelWriter.set(0, column.incrementAndGet(), total.get("subjectName").toString());
        }
    }

    private void fillTotalData(ExcelWriter excelWriter, List<Map<String, Object>> totals) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(1, column.incrementAndGet(), "总体");
        for(Map<String, Object> total : totals){
            excelWriter.set(1, column.incrementAndGet(), total.get("average"));
        }
    }

    @SuppressWarnings("unchecked")
    private void fillData(ExcelWriter excelWriter, List<Map<String, Object>> schools) {
        int row = 2;
        AtomicInteger column = new AtomicInteger(-1);
        for (Map<String, Object> school : schools){
            List<Map<String, Object>> subjects = (List<Map<String, Object>>)school.get("subjects");
            excelWriter.set(row, column.incrementAndGet(), school.get("schoolName"));
            for(Map<String, Object> subject : subjects){
                excelWriter.set(row, column.incrementAndGet(), subject.get("average"));
            }
            row++;
            column.set(-1);
        }
    }
}
