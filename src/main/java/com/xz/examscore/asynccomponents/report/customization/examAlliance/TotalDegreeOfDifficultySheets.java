package com.xz.examscore.asynccomponents.report.customization.examAlliance;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.ajia.Param;
import com.xz.examscore.api.server.customization.examAlliance.TotalDegreeOfDifficultyAnalysis;
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
public class TotalDegreeOfDifficultySheets extends SheetGenerator {

    @Autowired
    TotalDegreeOfDifficultyAnalysis totalDegreeOfDifficultyAnalysis;

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {
        Param param = new Param().setParameter("projectId", projectId);
        Result result = totalDegreeOfDifficultyAnalysis.execute(param);
        List<Map<String, Object>> totalDegreeOfDifficulty = result.get("totalDegreeOfDifficulty");
        setHeader(excelWriter, totalDegreeOfDifficulty);
        fillSchoolData(excelWriter, totalDegreeOfDifficulty);
    }

    private void setHeader(ExcelWriter excelWriter, List<Map<String, Object>> totalDegreeOfDifficulty) {
        AtomicInteger column = new AtomicInteger(-1);
        totalDegreeOfDifficulty.forEach(d -> excelWriter.set(0, column.incrementAndGet(), d.get("subjectName")));
    }

    private void fillSchoolData(ExcelWriter excelWriter, List<Map<String, Object>> totalDegreeOfDifficulty) {
        AtomicInteger column = new AtomicInteger(-1);
        totalDegreeOfDifficulty.forEach(d -> excelWriter.set(1, column.incrementAndGet(), d.get("rate")));
    }
}
