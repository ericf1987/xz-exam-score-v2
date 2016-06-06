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

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {
        List<String> schoolIds = schoolService.getProjectSchools(projectId).stream().
                map(d -> d.getString("school")).collect(Collectors.toList());
        Param param = new Param().setParameter("projectId", projectId).
                setParameter("schoolIds", schoolIds.toArray(new String[schoolIds.size()]));
        Result result = projectSubjectAnalysis.execute(param);
        System.out.println("学科分析data-->" + result.getData());
        setupHeader(excelWriter, result.get("totals"));
        fillProvinceData(result.get("totals"), excelWriter);
        fillSchoolData(result.getList("schools", null),excelWriter);
    }

    private void fillProvinceData(Map<String, Object> totals, ExcelWriter excelWriter) {
        //将汇总信息填充至第一行
        fillRow(totals, excelWriter, 1);
    }

    private void fillRow(Map<String, Object> totals, ExcelWriter excelWriter, int row) {
        AtomicInteger column = new AtomicInteger(-1);
        if(row == 1){
            excelWriter.set(row, column.incrementAndGet(), "总体");
        }else{
            excelWriter.set(row, column.incrementAndGet(), totals.get("schoolName"));
        }
        excelWriter.set(row, column.incrementAndGet(), totals.get("studentCount"));
        excelWriter.set(row, column.incrementAndGet(), totals.get("totalAvg"));
        List<Map<String, Object>> subjects = (List<Map<String, Object>>)totals.get("subjects");
        for(Map<String, Object> subject : subjects){
            excelWriter.set(row, column.incrementAndGet(), subject.get("subjectAvg"));
        }
    }

    private void fillSchoolData(List<Map<String, Object>> schools, ExcelWriter excelWriter) {
        int row = 2;
        for(Map<String, Object> school : schools){
            fillRow(school, excelWriter, row);
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
        }
    }
}
