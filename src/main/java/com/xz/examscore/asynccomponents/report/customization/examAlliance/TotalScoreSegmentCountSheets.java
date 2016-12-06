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
 * @author by fengye on 2016/12/6.
 */
@Component
public class TotalScoreSegmentCountSheets extends SheetGenerator {

    @Autowired
    TotalScoreSegmentCountAnalysis totalScoreSegmentCountAnalysis;

    public static final String[] COLUMN = new String[]{
            "学校", "900分以上", "900-800", "800-700", "700-600",
            "600-500", "500-400", "400-300", "300以下"
    };

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {
        Param param = new Param().setParameter("projectId", projectId);
        Result result = totalScoreSegmentCountAnalysis.execute(param);
        setHeader(excelWriter);
        fillProvinceData(excelWriter, result);
        fillSchoolData(excelWriter, result);
    }

    private void setHeader(ExcelWriter excelWriter) {
        AtomicInteger column = new AtomicInteger(-1);
        for (String c : COLUMN) {
            excelWriter.set(0, column.incrementAndGet(), c);
        }
    }

    private void fillProvinceData(ExcelWriter excelWriter, Result result) {
        AtomicInteger column = new AtomicInteger(-1);
        Map<String, Object> provinceData = result.get("project");
        if (null != provinceData && !provinceData.isEmpty()) {
            excelWriter.set(1, column.incrementAndGet(), provinceData.get("name"));
            CounterMap<Integer> counterMap = (CounterMap<Integer>) provinceData.get("data");
            List<Integer> keys = new ArrayList<>(counterMap.keySet());
            Collections.sort(keys, Collections.reverseOrder());
            for (Integer key : keys) {
                excelWriter.set(1, column.incrementAndGet(), counterMap.get(key));
            }
        }
    }

    private void fillSchoolData(ExcelWriter excelWriter, Result result) {
        AtomicInteger column = new AtomicInteger(-1);
        int row = 2;
        List<Map<String, Object>> schools = result.get("schools");
        for (Map<String, Object> schoolMap : schools) {
            excelWriter.set(row, column.incrementAndGet(), schoolMap.get("name"));
            CounterMap<Integer> counterMap = (CounterMap<Integer>) schoolMap.get("data");
            List<Integer> keys = new ArrayList<>(counterMap.keySet());
            Collections.sort(keys, Collections.reverseOrder());
            for (Integer key : keys) {
                excelWriter.set(row, column.incrementAndGet(), counterMap.get(key));
            }
            row++;
            column.set(-1);
        }
    }
}
