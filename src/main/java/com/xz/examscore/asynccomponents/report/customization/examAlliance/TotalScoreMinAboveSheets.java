package com.xz.examscore.asynccomponents.report.customization.examAlliance;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.server.customization.examAlliance.TotalScoreMinAboveAnalysis;
import com.xz.examscore.asynccomponents.report.SheetGenerator;
import com.xz.examscore.asynccomponents.report.SheetTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author by fengye on 2017/1/11.
 */
@Component
public class TotalScoreMinAboveSheets extends SheetGenerator {

    @Autowired
    TotalScoreMinAboveAnalysis totalScoreMinAboveAnalysis;

    @Autowired
    TotalScoreBySegSheets totalScoreBySegSheets;

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {
        Param param = new Param()
                .setParameter("projectId", projectId)
                .setParameter("max", "650")
                .setParameter("min", "350")
                .setParameter("span", "10");
        Result result = totalScoreMinAboveAnalysis.execute(param);
        List<String> column = result.get("column");
        Map<String, Object> provinceMap = result.get("provinceData");
        List<Map<String, Object>> schoolList = result.get("schoolData");
        totalScoreBySegSheets.setHeader(excelWriter, column);
        totalScoreBySegSheets.fillProvinceData(excelWriter, provinceMap);
        totalScoreBySegSheets.fillSchoolData(excelWriter, schoolList);
    }

}
