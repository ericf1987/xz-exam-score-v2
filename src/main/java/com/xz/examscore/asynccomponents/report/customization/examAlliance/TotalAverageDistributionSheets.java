package com.xz.examscore.asynccomponents.report.customization.examAlliance;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.server.customization.examAlliance.TotalAverageDistributionAnalysis;
import com.xz.examscore.asynccomponents.report.SheetGenerator;
import com.xz.examscore.asynccomponents.report.SheetTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author by fengye on 2016/12/27.
 */
@SuppressWarnings("Duplicates")
@Component
public class TotalAverageDistributionSheets extends SheetGenerator {

    @Autowired
    TotalAverageDistributionAnalysis totalAverageDistributionAnalysis;

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {
        Param param = new Param().setParameter("projectId", projectId);
        Result result = totalAverageDistributionAnalysis.execute(param);
        Map<String, Object> data = result.get("totalAverageDistribution");
        List<Map<String, Object>> schoolData = (List<Map<String, Object>>) data.get("schoolData");
        Map<String, Object> totalData = (Map<String, Object>) data.get("totalData");
        setHeader(excelWriter, totalData);
        fillProvinceData(excelWriter, totalData);
        fillSchoolData(excelWriter, schoolData);
    }

    private void setHeader(ExcelWriter excelWriter, Map<String, Object> totalData) {
        AtomicInteger column = new AtomicInteger(-1);
        List<Map<String, Object>> subjects = (List<Map<String, Object>>) totalData.get("subjects");
        excelWriter.set(0, column.incrementAndGet(), "学校名称");
        subjects.forEach(subject -> excelWriter.set(0, column.incrementAndGet(), subject.get("subjectName")));
    }

    private void fillProvinceData(ExcelWriter excelWriter, Map<String, Object> totalData) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(1, column.incrementAndGet(), totalData.get("schoolName"));
        List<Map<String, Object>> subjects = (List<Map<String, Object>>) totalData.get("subjects");
        subjects.forEach(subject -> excelWriter.set(1, column.incrementAndGet(), subject.get("average")));
    }

    private void fillSchoolData(ExcelWriter excelWriter, List<Map<String, Object>> schoolData) {
        int row = 2;
        AtomicInteger column = new AtomicInteger(-1);
        for (Map<String, Object> schoolMap : schoolData) {
            List<Map<String, Object>> subjects = (List<Map<String, Object>>) schoolMap.get("subjects");
            excelWriter.set(row, column.incrementAndGet(), schoolMap.get("schoolName"));
            for(Map<String, Object> subject : subjects){
                excelWriter.set(row, column.incrementAndGet(), subject.get("average"));
            }
            row++;
            column.set(-1);
        }
    }
}
