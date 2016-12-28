package com.xz.examscore.asynccomponents.report.customization.examAlliance;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.server.customization.examAlliance.ToBeEntryLevelAnalysis;
import com.xz.examscore.asynccomponents.report.SheetGenerator;
import com.xz.examscore.asynccomponents.report.SheetTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author by fengye on 2016/12/28.
 */
@Component
public class ToBeEntryLevelSheets extends SheetGenerator {

    @Autowired
    ToBeEntryLevelAnalysis toBeEntryLevelAnalysis;

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {
        Param param = new Param().setParameter("projectId", projectId);
        Result result = toBeEntryLevelAnalysis.execute(param);
        Map<String, Object> provinceData = result.get("provinceData");
        List<Map<String, Object>> schoolData = result.get("schoolData");
        setHeader(excelWriter);
        fillProvinceData(excelWriter, provinceData);
        fillSchoolData(excelWriter, schoolData);
    }

    private void setHeader(ExcelWriter excelWriter) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(0, column.incrementAndGet(), "学校名称");
        excelWriter.set(0, column.incrementAndGet(), "实考人数");
        excelWriter.set(0, column.incrementAndGet(), "一本人数");
        excelWriter.set(0, column.incrementAndGet(), "一本上线率");
        excelWriter.set(0, column.incrementAndGet(), "临界生人数");
        excelWriter.set(0, column.incrementAndGet(), "一本入围人数");
        excelWriter.set(0, column.incrementAndGet(), "入围比率");
        excelWriter.set(0, column.incrementAndGet(), "增率");
    }

    private void fillProvinceData(ExcelWriter excelWriter, Map<String, Object> provinceData) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(1, column.incrementAndGet(), provinceData.get("schoolName"));
        excelWriter.set(1, column.incrementAndGet(), provinceData.get("studentCount"));
        excelWriter.set(1, column.incrementAndGet(), provinceData.get("oneCount"));
        excelWriter.set(1, column.incrementAndGet(), provinceData.get("oneRate"));
        excelWriter.set(1, column.incrementAndGet(), provinceData.get("requiredCount"));
        excelWriter.set(1, column.incrementAndGet(), provinceData.get("count_to_be"));
        excelWriter.set(1, column.incrementAndGet(), provinceData.get("onlineRate_to_be"));
        excelWriter.set(1, column.incrementAndGet(), provinceData.get("uprate"));
    }

    private void fillSchoolData(ExcelWriter excelWriter, List<Map<String, Object>> schoolData) {
        int row = 2;
        AtomicInteger column = new AtomicInteger(-1);
        for(Map<String, Object> schoolMap : schoolData){
            excelWriter.set(row, column.incrementAndGet(), schoolMap.get("schoolName"));
            excelWriter.set(row, column.incrementAndGet(), schoolMap.get("studentCount"));
            excelWriter.set(row, column.incrementAndGet(), schoolMap.get("oneCount"));
            excelWriter.set(row, column.incrementAndGet(), schoolMap.get("oneRate"));
            excelWriter.set(row, column.incrementAndGet(), schoolMap.get("requiredCount"));
            excelWriter.set(row, column.incrementAndGet(), schoolMap.get("count_to_be"));
            excelWriter.set(row, column.incrementAndGet(), schoolMap.get("onlineRate_to_be"));
            excelWriter.set(row, column.incrementAndGet(), schoolMap.get("uprate"));
            row++;
            column.set(-1);
        }
    }
}
