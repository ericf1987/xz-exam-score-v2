package com.xz.report.schools;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.api.server.school.SchoolObjectiveAnalysis;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.report.SheetGenerator;
import com.xz.report.SheetTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author by fengye on 2016/6/19.
 */
@Component
public class SchoolPaperObjectiveSheets extends SheetGenerator{
    @Autowired
    SchoolObjectiveAnalysis schoolObjectiveAnalysis;

    public static final String[] SECONDARY_COLUMN = new String[]{
            "A率", "B率", "C率", "D率", "不选率", "区分度"
    };

    public static final Map<String, String> ITEM_MAPPINGS = new LinkedHashMap<>();

    static {
        ITEM_MAPPINGS.put("A率", "A");
        ITEM_MAPPINGS.put("B率", "B");
        ITEM_MAPPINGS.put("C率", "C");
        ITEM_MAPPINGS.put("D率", "D");
        ITEM_MAPPINGS.put("不选率", "unSelect");
        ITEM_MAPPINGS.put("区分度", "questDeviation");
    }

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {
        Target target = sheetTask.get("target");
        String subjectId = target.match(Target.PROJECT) ? null : target.getId().toString();
        Range schoolRange = sheetTask.getRange();
        Param param = new Param().setParameter("projectId", projectId).
                setParameter("schoolId", schoolRange.getId()).setParameter("subjectId", subjectId);
        Result result = schoolObjectiveAnalysis.execute(param);
        System.out.println("学校客观题分析-->" + result.getData());
        setupHeader(excelWriter, result.get("classes"));
        fillData(excelWriter, result);
    }

    private void setupHeader(ExcelWriter excelWriter, List<Map<String, Object>> classes) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(0, column.incrementAndGet(), "客观题");
        excelWriter.mergeCells(0, column.get(), 0, column.incrementAndGet());
        excelWriter.set(0, column.incrementAndGet(), "本校");
        for (Map<String, Object> clazz : classes) {
            excelWriter.set(0, column.incrementAndGet(), clazz.get("className"));
        }
    }

    private void fillData(ExcelWriter excelWriter, Result result) {
        int row = 1;
        List<Map<String, Object>> schools = result.get("schools");
        List<Map<String, Object>> classes = result.get("classes");
        for (int index = 0; index < schools.size(); index++) {
            int currentrow = fillOneQuestData(row, excelWriter, index, schools.get(index), classes);
            excelWriter.mergeCells(row, 0, row + 5, 0);
            row = currentrow;
        }
    }

    @SuppressWarnings("unchecked")
    private int fillOneQuestData(int row, ExcelWriter excelWriter, int index, Map<String, Object> total, List<Map<String, Object>> classes) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(row, column.incrementAndGet(), total.get("questNo").toString());
        for (String s : SECONDARY_COLUMN) {
            excelWriter.set(row, column.incrementAndGet(), s);
            String totalRate = getRate(ITEM_MAPPINGS.get(s), total);
            //填写总体数据
            excelWriter.set(row, column.incrementAndGet(), totalRate);
            for (Map<String, Object> claszz : classes) {
                //获取所有客观题
                List<Map<String, Object>> objectives = (List<Map<String, Object>>) claszz.get("objectives");
                //获取指定位置的客观题
                Map<String, Object> objective = objectives.get(index);
                String rate = getRate(ITEM_MAPPINGS.get(s), objective);
                excelWriter.set(row, column.incrementAndGet(), rate);
            }
            row++;
            column.set(0);
        }
        return row;
    }

    //根据获取指定选项的得分率
    @SuppressWarnings("unchecked")
    private String getRate(String optionKey, Map<String, Object> objective) {
        if(StringUtils.isEmpty(objective.get(optionKey))){
            return "0";
        }else{
            if(optionKey.equals("questDeviation")){
                return objective.get(optionKey).toString();
            }else{
                Map<String, Object> totalOneOption = (Map<String, Object>) objective.get(optionKey);
                return totalOneOption.get("rate").toString();
            }
        }
    }

}
