package com.xz.report.total;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.api.server.project.ProjectObjectiveAnalysis;
import com.xz.bean.Target;
import com.xz.report.SheetGenerator;
import com.xz.report.SheetTask;
import com.xz.services.SchoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author by fengye on 2016/6/17.
 * 总体成绩分析/试卷分析/客观题分析
 */
@Component
public class TotalPaperObjectiveSheets extends SheetGenerator {

    @Autowired
    SchoolService schoolService;

    @Autowired
    ProjectObjectiveAnalysis projectObjectiveAnalysis;

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
        List<String> schoolIds = schoolService.getProjectSchools(projectId).stream().
                map(d -> d.getString("school")).collect(Collectors.toList());
        Param param = new Param().setParameter("projectId", projectId).
                setParameter("subjectId", subjectId).
                setParameter("schoolIds", schoolIds.toArray(new String[schoolIds.size()]));
        Result result = projectObjectiveAnalysis.execute(param);
        //System.out.println("客观题分析-->" + result.getData());
        setupHeader(excelWriter, result.get("schools"));
        fillData(excelWriter, result);
    }

    private void setupHeader(ExcelWriter excelWriter, List<Map<String, Object>> schools) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(0, column.incrementAndGet(), "客观题");
        excelWriter.mergeCells(0, column.get(), 0, column.incrementAndGet());
        excelWriter.set(0, column.incrementAndGet(), "总体");
        for (Map<String, Object> school : schools) {
            excelWriter.set(0, column.incrementAndGet(), school.get("schoolName"));
        }
    }

    private void fillData(ExcelWriter excelWriter, Result result) {
        int row = 1;
        List<Map<String, Object>> totals = result.get("totals");
        List<Map<String, Object>> schools = result.get("schools");
        for (int index = 0; index < totals.size(); index++) {
            int currentrow = fillOneQuestData(row, excelWriter, index, totals.get(index), schools);
            excelWriter.mergeCells(row, 0, row + 5, 0);
            row = currentrow;
        }
    }

    @SuppressWarnings("unchecked")
    private int fillOneQuestData(int row, ExcelWriter excelWriter, int index, Map<String, Object> total, List<Map<String, Object>> schools) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(row, column.incrementAndGet(), total.get("questNo").toString());
        for (String s : SECONDARY_COLUMN) {
            excelWriter.set(row, column.incrementAndGet(), s);
            String totalRate = getRate(ITEM_MAPPINGS.get(s), total);
            //填写总体数据
            excelWriter.set(row, column.incrementAndGet(), totalRate);
            for (Map<String, Object> school : schools) {
                //获取所有客观题
                List<Map<String, Object>> objectives = (List<Map<String, Object>>) school.get("objectives");
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
