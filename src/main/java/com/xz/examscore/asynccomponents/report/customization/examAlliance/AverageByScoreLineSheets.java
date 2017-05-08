package com.xz.examscore.asynccomponents.report.customization.examAlliance;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.ajia.Param;
import com.xz.examscore.api.server.customization.examAlliance.AverageByScoreLineAnalysis;
import com.xz.examscore.asynccomponents.report.SheetGenerator;
import com.xz.examscore.asynccomponents.report.SheetTask;
import com.xz.examscore.services.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author by fengye on 2016/12/5.
 */
@Component
public class AverageByScoreLineSheets extends SheetGenerator {

    @Autowired
    AverageByScoreLineAnalysis averageByScoreLineAnalysis;

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {
        Param param = new Param().setParameter("projectId", projectId);
        Result result = averageByScoreLineAnalysis.execute(param);
        setHeader(excelWriter, result);
        fillProvinceData(excelWriter, result);
        fillSchoolsData(excelWriter, result);
    }

    private void setHeader(ExcelWriter excelWriter, Result result) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(0, column.incrementAndGet(), "学校");
        excelWriter.set(0, column.incrementAndGet(), "人数");
        excelWriter.set(0, column.incrementAndGet(), "全科平均分");
        List<String> subjectIds = result.get("subjectIds");
        for(String subjectId : subjectIds){
            excelWriter.set(0, column.incrementAndGet(), SubjectService.getSubjectName(subjectId) + "平均分");
        }
    }

    private void fillProvinceData(ExcelWriter excelWriter, Result result) {
        AtomicInteger column = new AtomicInteger(-1);
        Map<String, Object> provinceData = result.get("provinceData");
        excelWriter.set(1, column.incrementAndGet(), "总体");
        excelWriter.set(1, column.incrementAndGet(), provinceData.get("count"));
        excelWriter.set(1, column.incrementAndGet(), provinceData.get("projectAverage"));
        List<Map<String, Object>> subjects = (List<Map<String, Object>>)provinceData.get("subjects");
        for (Map<String, Object> subjectMap : subjects){
            excelWriter.set(1, column.incrementAndGet(), subjectMap.get("average"));
        }
    }

    private void fillSchoolsData(ExcelWriter excelWriter, Result result) {
        AtomicInteger column = new AtomicInteger(-1);
        int row = 2;
        List<Map<String, Object>> schoolsData = result.get("schoolsData");
        for(Map<String, Object> schoolMap : schoolsData){
            excelWriter.set(row, column.incrementAndGet(), schoolMap.get("schoolName"));
            excelWriter.set(row, column.incrementAndGet(), schoolMap.get("count"));
            excelWriter.set(row, column.incrementAndGet(), schoolMap.get("projectAverage"));
            List<Map<String, Object>> subjects = (List<Map<String, Object>>)schoolMap.get("subjects");
            for (Map<String, Object> subjectMap : subjects){
                excelWriter.set(row, column.incrementAndGet(), subjectMap.get("average"));
            }
            row++;
            column.set(-1);
        }
    }
}
