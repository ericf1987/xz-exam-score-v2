package com.xz.report.schools;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.api.server.school.SchoolPointAbilityLevelAnalysis;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.report.SheetGenerator;
import com.xz.report.SheetTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author by fengye on 2016/7/1.
 */
@Component
public class SchoolPointAbilityLevelSheets extends SheetGenerator {
    @Autowired
    SchoolPointAbilityLevelAnalysis schoolPointAbilityLevelAnalysis;

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {
        Target target = sheetTask.get("target");
        String subjectId = target.getId().toString();
        Range schoolRange = sheetTask.getRange();
        Param param = new Param().setParameter("projectId", projectId)
                .setParameter("subjectId", subjectId)
                .setParameter("schoolId", schoolRange.getId());
        Result result = schoolPointAbilityLevelAnalysis.execute(param);
        setupHeader(excelWriter, result);
        setupSecondaryHeader(excelWriter, result);
        fillDetailData(excelWriter, result);
    }

    public void setupHeader(ExcelWriter excelWriter, Result result) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(0, column.incrementAndGet(), "双向细目");
        Map<String, Object> levels = result.get("levels");
        List<Map<String, Object>> levelInfos = (List<Map<String, Object>>)levels.get("levelInfos");
        for(Map<String, Object> levelInfo : levelInfos){
            excelWriter.set(0, column.incrementAndGet(), "能力层级" + levelInfo.get("name"));
            column.incrementAndGet();
            excelWriter.mergeCells(0, column.get() - 1, 0, column.get());
        }
        excelWriter.set(0, column.incrementAndGet(), "合计");
        excelWriter.mergeCells(0, column.get(), 1, column.get());
    }

    public void setupSecondaryHeader(ExcelWriter excelWriter, Result result) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(1, column.incrementAndGet(), "双向细目");
        excelWriter.mergeCells(0, column.get(), 1, column.get());
        Map<String, Object> levels = result.get("levels");
        List<Map<String, Object>> levelInfos = (List<Map<String, Object>>)levels.get("levelInfos");
        for(int i = 0; i < levelInfos.size(); i++){
            excelWriter.set(1, column.incrementAndGet(), "对应题号");
            excelWriter.set(1, column.incrementAndGet(), "总体得分");
        }
    }

    public void fillDetailData(ExcelWriter excelWriter, Result result) {
        int row = 2;
        AtomicInteger column = new AtomicInteger(-1);
        List<Map<String, Object>> points = result.get("points");
        for(Map<String, Object> point : points){
            excelWriter.set(row, column.incrementAndGet(), point.get("pointName"));
            List<Map<String, Object>> pointLevels = (List<Map<String, Object>>)point.get("pointLevels");
            for(Map<String, Object> pointLevel : pointLevels){
                List<String> questNosList = (List<String>)pointLevel.get("questNos");
                String questNos = questNosList.size() == 0 ? "" : split(questNosList, ",");
                String avgScore = pointLevel.get("avgScore") == null ? "" : pointLevel.get("avgScore").toString();
                excelWriter.set(row, column.incrementAndGet(), questNos);
                excelWriter.set(row, column.incrementAndGet(), avgScore);
            }
            excelWriter.set(row, column.incrementAndGet(), point.get("score"));
            row ++;
            column.set(-1);
        }
    }

    public String split(List<String> list, String fix){
        StringBuilder builder = new StringBuilder();
        for(String s : list){
            builder.append(s).append(fix);
        }
        return builder.toString().substring(0, builder.toString().length() - 1);
    }
}
