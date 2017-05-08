package com.xz.examscore.asynccomponents.report.customization.examAlliance;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.ajia.Param;
import com.xz.examscore.api.server.customization.examAlliance.QuestScoreDetailAnalysis;
import com.xz.examscore.asynccomponents.report.SheetGenerator;
import com.xz.examscore.asynccomponents.report.SheetTask;
import com.xz.examscore.bean.Target;
import com.xz.examscore.util.DoubleUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author by fengye on 2016/12/4.
 */
@SuppressWarnings("unchecked")
@Component
public class QuestScoreDetailSheets extends SheetGenerator {

    @Autowired
    QuestScoreDetailAnalysis questScoreDetailAnalysis;

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {
        Target target = sheetTask.get("target");
        Param param = new Param().setParameter("projectId", projectId)
                .setParameter("subjectId", target.getId().toString());
        Result result = questScoreDetailAnalysis.execute(param);
        setHeader(excelWriter, result);
        fillData(excelWriter, result);
    }

    private void setHeader(ExcelWriter excelWriter, Result result) {
        List<String> questNos = result.get("questNos");
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(0, column.incrementAndGet(), "题号");
        for(String questNo : questNos){
            excelWriter.set(0, column.incrementAndGet(), questNo);
        }
    }

    private void fillData(ExcelWriter excelWriter, Result result) {
        List<Map<String, Object>> schools = result.get("schools");
        AtomicInteger column = new AtomicInteger(-1);
        int row = 1;
        for (Map<String, Object> schoolMap : schools){
            String schoolName = MapUtils.getString(schoolMap, "schoolName");
            excelWriter.set(row, column.incrementAndGet(), schoolName);
            List<Map<String, Object>> quests = (List<Map<String, Object>>)MapUtils.getObject(schoolMap, "quests");
            for (Map<String, Object> questMap : quests){
                double rate = MapUtils.getDoubleValue(questMap, "rate");
                excelWriter.set(row, column.incrementAndGet(), DoubleUtils.toPercent(rate));
            }
            row++;
            column.set(-1);
        }
    }
}
