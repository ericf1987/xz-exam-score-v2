package com.xz.examscore.asynccomponents.report.customization.examAlliance;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.ajia.Param;
import com.xz.examscore.api.server.customization.examAlliance.CollegeEntryLevelRateAnalysis;
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
@Component
public class CollegeEntryLevelRateSheets extends SheetGenerator {

    @Autowired
    CollegeEntryLevelRateAnalysis collegeEntryLevelRateAnalysis;

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {
        Param param = new Param().setParameter("projectId", projectId);
        Result result = collegeEntryLevelRateAnalysis.execute(param);
        List<Map<String, Object>> schoolData = result.get("schoolCollegeEntryLevel");
        Map<String, Object> provinceData = result.get("projectCollegeEntryLevel");

        setHeader(excelWriter);
        fillProvinceData(excelWriter, provinceData);
        fillSchoolData(excelWriter, schoolData);
    }

    private void setHeader(ExcelWriter excelWriter) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(0, column.incrementAndGet(), "学校名称");
        excelWriter.set(0, column.incrementAndGet(), "一本人数");
        excelWriter.set(0, column.incrementAndGet(), "一本上线率");
        excelWriter.set(0, column.incrementAndGet(), "二本人数");
        excelWriter.set(0, column.incrementAndGet(), "二本上线率");
    }

    private void fillProvinceData(ExcelWriter excelWriter, Map<String, Object> provinceData) {
        AtomicInteger column = new AtomicInteger(-1);
        List<Map<String, Object>> entryLevelList = (List<Map<String, Object>>)provinceData.get("entryLevelList");
        excelWriter.set(1, column.incrementAndGet(), provinceData.get("schoolName"));
        entryLevelList.forEach(entryLevel -> {
            excelWriter.set(1, column.incrementAndGet(), entryLevel.get("count"));
            excelWriter.set(1, column.incrementAndGet(), entryLevel.get("rate"));
        });
    }

    private void fillSchoolData(ExcelWriter excelWriter, List<Map<String, Object>> schoolData) {
        int row = 2;
        AtomicInteger column = new AtomicInteger(-1);
        for(Map<String, Object> schoolMap : schoolData){
            List<Map<String, Object>> entryLevelList = (List<Map<String, Object>>)schoolMap.get("entryLevelList");
            excelWriter.set(row, column.incrementAndGet(), schoolMap.get("schoolName"));
            for(Map<String, Object> entryLevel : entryLevelList){
                excelWriter.set(row, column.incrementAndGet(), entryLevel.get("count"));
                excelWriter.set(row, column.incrementAndGet(), entryLevel.get("rate"));
            }
            row++;
            column.set(-1);
        }
    }
}
