package com.xz.examscore.asynccomponents.report.customization.examAlliance;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.ajiaedu.common.lang.CounterMap;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.server.customization.examAlliance.TotalScoreSegmentCountAnalysis;
import com.xz.examscore.asynccomponents.report.SheetGenerator;
import com.xz.examscore.asynccomponents.report.SheetTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author by fengye on 2017/1/10.
 */
@Component
public class TotalScoreBySegSheets extends SheetGenerator {

    @Autowired
    TotalScoreSegmentCountAnalysis totalScoreSegmentCountAnalysis;

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {
        Param param = new Param().setParameter("projectId", projectId)
                .setParameter("max", "650")
                .setParameter("min", "350")
                .setParameter("span", "10");
        Result result = totalScoreSegmentCountAnalysis.execute(param);
        List<String> column = result.get("column");
        Map<String, Object> provinceMap = result.get("project");
        List<Map<String, Object>> schoolList = result.get("schools");
        setHeader(excelWriter, column);
        fillProvinceData(excelWriter, provinceMap);
        fillSchoolData(excelWriter, schoolList);
    }

    private void setHeader(ExcelWriter excelWriter, List<String> col) {
        AtomicInteger column = new AtomicInteger(-1);
        int row = 0;
        excelWriter.set(row, column.incrementAndGet(), "学校");
        excelWriter.set(row, column.incrementAndGet(), "最高分");
        for (String c : col) {
            excelWriter.set(row, column.incrementAndGet(), c);
        }
        excelWriter.set(row, column.incrementAndGet(), "总人数");
    }

    private void fillProvinceData(ExcelWriter excelWriter, Map<String, Object> provinceMap) {
        AtomicInteger column = new AtomicInteger(-1);
        int row = 1;
        CounterMap<Integer> counterMap = (CounterMap<Integer>) provinceMap.get("data");
        List<Integer> keys = new ArrayList<>(counterMap.keySet());
        Collections.sort(keys);
        excelWriter.set(row, column.incrementAndGet(), provinceMap.get("name"));
        excelWriter.set(row, column.incrementAndGet(), provinceMap.get("max"));
        for (Integer key : keys) {
            excelWriter.set(row, column.incrementAndGet(), counterMap.get(key));
        }
        excelWriter.set(row, column.incrementAndGet(), provinceMap.get("count"));
    }

    private void fillSchoolData(ExcelWriter excelWriter, List<Map<String, Object>> schoolList) {
        AtomicInteger column = new AtomicInteger(-1);
        int row = 2;
        for (Map<String, Object> school : schoolList) {
            CounterMap<Integer> counterMap = (CounterMap<Integer>) school.get("data");
            List<Integer> keys = new ArrayList<>(counterMap.keySet());
            Collections.sort(keys);
            excelWriter.set(row, column.incrementAndGet(), school.get("name"));
            excelWriter.set(row, column.incrementAndGet(), school.get("max"));
            for (Integer key : keys) {
                excelWriter.set(row, column.incrementAndGet(), counterMap.get(key));
            }
            excelWriter.set(row, column.incrementAndGet(), school.get("count"));
            row++;
            column.set(-1);
        }
    }
}
