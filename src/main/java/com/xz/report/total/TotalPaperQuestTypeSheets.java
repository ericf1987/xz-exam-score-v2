package com.xz.report.total;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.api.server.project.ProjectQuestTypeAnalysis;
import com.xz.bean.Target;
import com.xz.report.SheetGenerator;
import com.xz.report.SheetTask;
import com.xz.services.SchoolService;
import com.xz.util.DoubleUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author by fengye on 2016/6/17.
 * 总体成绩分析/试卷分析/试卷题型分析/
 */
@SuppressWarnings("unchecked")
@Component
public class TotalPaperQuestTypeSheets extends SheetGenerator {
    @Autowired
    ProjectQuestTypeAnalysis projectQuestTypeAnalysis;

    @Autowired
    SchoolService schoolService;

    public static final String[] SECONDARY_COLUMN = new String[]{
            "分数","得分率"
    };

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {
        Target target = sheetTask.get("target");
        String subjectId = target.match(Target.PROJECT) ? null : target.getId().toString();

        List<String> schoolIds = schoolService.getProjectSchools(projectId).stream().
                map(d -> d.getString("school")).collect(Collectors.toList());
        Param param = new Param().setParameter("projectId", projectId).
                setParameter("subjectId", subjectId).
                setParameter("schoolIds", schoolIds.toArray(new String[schoolIds.size()]));
        Result result = projectQuestTypeAnalysis.execute(param);
        System.out.println("总体试卷题型分析-->" + result.getData());
        setupHeader(excelWriter, result.get("totals"));
        setupSecondaryHeader(excelWriter, result.get("totals"));
        fillTotalData(excelWriter, result.get("totals"));
        fillData(excelWriter, result.get("schools"));
    }

    private void setupHeader(ExcelWriter excelWriter, List<Map<String, Object>> totals) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(0, column.incrementAndGet(), "试题");
        for(Map<String, Object> total : totals){
            //题型名称
            excelWriter.set(0, column.incrementAndGet(), total.get("name"));
            excelWriter.mergeCells(0, column.get(), 0, column.incrementAndGet());
        }
    }

    private void setupSecondaryHeader(ExcelWriter excelWriter, List<Map<String, Object>> totals) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(1, column.incrementAndGet(), "试题");
        excelWriter.mergeCells(0, column.get(), 1, column.get());
        for(int i = 0; i < totals.size();i++){
            excelWriter.set(1, column.incrementAndGet(), SECONDARY_COLUMN[0]);
            excelWriter.set(1, column.incrementAndGet(), SECONDARY_COLUMN[1]);
        }
    }

    private void fillTotalData(ExcelWriter excelWriter, List<Map<String, Object>> totals) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(2, column.incrementAndGet(), "总体");
        for(Map<String, Object> total : totals){
            excelWriter.set(2, column.incrementAndGet(), total.get("score"));
            excelWriter.set(2, column.incrementAndGet(), DoubleUtils.toPercent(Double.parseDouble(total.get("scoreRate").toString())));
        }
    }

    private void fillData(ExcelWriter excelWriter, List<Map<String, Object>> schools) {
        int row = 3;
        AtomicInteger column = new AtomicInteger(-1);
        for(Map<String, Object> school : schools){
            excelWriter.set(row, column.incrementAndGet(), school.get("schoolName"));
            List<Map<String, Object>> questTypes = (List<Map<String, Object>>)school.get("questTypes");
            for(Map<String, Object> questType : questTypes){
                excelWriter.set(row, column.incrementAndGet(), questType.get("score"));
                excelWriter.set(row, column.incrementAndGet(), DoubleUtils.toPercent(Double.parseDouble(questType.get("scoreRate").toString())));
            }
            row++;
            column.set(-1);
        }
    }
}
