package com.xz.examscore.asynccomponents.report.customization.examAlliance;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.ajia.Param;
import com.xz.examscore.api.server.customization.examAlliance.QuestScoreMaxAndMin;
import com.xz.examscore.asynccomponents.report.SheetGenerator;
import com.xz.examscore.asynccomponents.report.SheetTask;
import com.xz.examscore.bean.Target;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author by fengye on 2016/12/4.
 */
@Component
public class ObjQuestScoreMaxAndMinSheets extends SheetGenerator {

    @Autowired
    QuestScoreMaxAndMin questScoreMaxAndMin;

    public static final String[] HEADER = new String[]{
            "得分率最高的客观题", "得分率最低的客观题"
    };

    public static final String[] SECONDARY_HEADER = new String[]{
            "小题", "得分", "得分率", "对应知识点"
    };

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {
        Target target = sheetTask.get("target");
        Param param = new Param().setParameter("projectId", projectId)
                .setParameter("subjectId", target.getId().toString())
                .setParameter("isObjective", true);
        Result result = questScoreMaxAndMin.execute(param);
        setHeader(excelWriter, HEADER);
        setSecondaryHeader(excelWriter);
        fillData(excelWriter, result);
    }

    public void setHeader(ExcelWriter excelWriter, String[] header) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(0, column.incrementAndGet(), "");
        for (int j = 0; j < header.length; j++) {
            for (int i = 0; i < SECONDARY_HEADER.length; i++) {
                excelWriter.set(0, column.incrementAndGet(), HEADER[j]);
            }
            excelWriter.mergeCells(0, column.get() - SECONDARY_HEADER.length + 1, 0, column.get());
        }
    }

    public void setSecondaryHeader(ExcelWriter excelWriter) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(1, column.incrementAndGet(), "学校名称");
        for (int i = 0; i < HEADER.length; i++) {
            for (int j = 0; j < SECONDARY_HEADER.length; j++) {
                excelWriter.set(1, column.incrementAndGet(), SECONDARY_HEADER[j]);
            }
        }
    }

    public void fillData(ExcelWriter excelWriter, Result result) {
        AtomicInteger column = new AtomicInteger(-1);
        int row = 2;
        List<Map<String, Object>> schools = result.get("schools");
        for(Map<String, Object> schoolMap : schools){
            List<Map<String, Object>> questList = (List<Map<String, Object>>)MapUtils.getObject(schoolMap, "questList");
            String schoolName = MapUtils.getString(schoolMap, "name");
            excelWriter.set(row, column.incrementAndGet(), schoolName);
            for(Map<String, Object> questMap : questList){
                excelWriter.set(row, column.incrementAndGet(), MapUtils.getString(questMap, "questNo"));
                excelWriter.set(row, column.incrementAndGet(), MapUtils.getString(questMap, "average"));
                excelWriter.set(row, column.incrementAndGet(), MapUtils.getString(questMap, "rate"));
                excelWriter.set(row, column.incrementAndGet(), MapUtils.getObject(questMap, "pointList"));
            }
            row++;
            column.set(-1);
        }
    }
}
