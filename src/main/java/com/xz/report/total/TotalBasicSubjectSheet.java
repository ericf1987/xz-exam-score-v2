package com.xz.report.total;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.api.server.project.ProjectSubjectAnalysis;
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
 * @author by fengye on 2016/6/6.
 */
@Component
public class TotalBasicSubjectSheet extends SheetGenerator {
    @Autowired
    ProjectSubjectAnalysis projectSubjectAnalysis;

    @Autowired
    SchoolService schoolService;

    public static final String[] SECONDARY_HEADER = new String[]{
            "平均分", "贡献度", "T分值"
    };

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {
        List<String> schoolIds = schoolService.getProjectSchools(projectId).stream().
                map(d -> d.getString("school")).collect(Collectors.toList());
        Param param = new Param().setParameter("projectId", projectId).
                setParameter("schoolIds", schoolIds.toArray(new String[schoolIds.size()]));
        Result result = projectSubjectAnalysis.execute(param);
        //System.out.println("学科分析data-->" + result.getData());
        setupHeader(excelWriter, result.get("totals"));
        setupSecondaryHeader(excelWriter, result.get("totals"));
        fillProvinceData(result.get("totals"), excelWriter);
        fillSchoolData(result.getList("schools", null),excelWriter);
    }

    private void fillProvinceData(Map<String, Object> totals, ExcelWriter excelWriter) {
        int row = 2;
        AtomicInteger column = new AtomicInteger(-1);
        fillRow(totals, excelWriter, 2);
    }

    private void fillRow(Map<String, Object> totals, ExcelWriter excelWriter, int row) {
        AtomicInteger column = new AtomicInteger(-1);
        if(row == 2){
            excelWriter.set(row, column.incrementAndGet(), "总体");
        }else{
            excelWriter.set(row, column.incrementAndGet(), totals.get("schoolName"));
        }
        excelWriter.set(row, column.incrementAndGet(), totals.get("studentCount"));
        excelWriter.set(row, column.incrementAndGet(), totals.get("totalAvg"));
        List<Map<String, Object>> subjects = (List<Map<String, Object>>)totals.get("subjects");
        for(Map<String, Object> subject : subjects){
            excelWriter.set(row, column.incrementAndGet(), subject.get("subjectAvg"));
            excelWriter.set(row, column.incrementAndGet(), subject.get("subjectRate"));
            excelWriter.set(row, column.incrementAndGet(), subject.get("tScore"));
        }
    }

    private void fillSchoolData(List<Map<String, Object>> schools, ExcelWriter excelWriter) {
        int row = 3;
        for(Map<String, Object> school : schools){
            fillRow(school, excelWriter, row);
            row++;
        }
    }

    private void setupHeader(ExcelWriter excelWriter, Map<String, Object> para) {
        //获取totals节点的学科信息
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(0, column.incrementAndGet(), "学校名称");
        excelWriter.set(0, column.incrementAndGet(), "实考人数");
        excelWriter.set(0, column.incrementAndGet(), "总平均分");
        List<Map<String, Object>> subjects = (List<Map<String, Object>>)para.get("subjects");
        for(Map subject : subjects){
            excelWriter.set(0, column.incrementAndGet(), subject.get("subjectName").toString());
            column.incrementAndGet();
            column.incrementAndGet();
            excelWriter.mergeCells(0, column.get() - 2, 0, column.get());
        }
    }

    private void setupSecondaryHeader(ExcelWriter excelWriter, Map<String, Object> schools) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(1, column.incrementAndGet(), "学校名称");
        excelWriter.set(1, column.incrementAndGet(), "实考人数");
        excelWriter.set(1, column.incrementAndGet(), "总平均分");
        excelWriter.mergeCells(0, 0, 1, 0);
        excelWriter.mergeCells(0, 1, 1, 1);
        excelWriter.mergeCells(0, 2, 1, 2);
        List<Map<String, Object>> subjects = (List<Map<String, Object>>) schools.get("subjects");
        for (Map<String, Object> subject : subjects) {
            excelWriter.set(1, column.incrementAndGet(), SECONDARY_HEADER[0]);
            excelWriter.set(1, column.incrementAndGet(), SECONDARY_HEADER[1]);
            excelWriter.set(1, column.incrementAndGet(), SECONDARY_HEADER[2]);
        }
    }
}
