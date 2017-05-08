package com.xz.examscore.asynccomponents.report.schools;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.ajia.Param;
import com.xz.examscore.api.server.classes.ClassRankLevelAnalysis;
import com.xz.examscore.api.server.school.SchoolRankLevelAnalysis;
import com.xz.examscore.asynccomponents.report.SheetGenerator;
import com.xz.examscore.asynccomponents.report.SheetTask;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.ProjectConfigService;
import com.xz.examscore.util.DoubleUtils;
import com.xz.examscore.util.RankLevelFormater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author by fengye on 2016/7/18.
 * 学校成绩分析-基础分析-等第分析
 */
@SuppressWarnings("unchecked")
@Component
public class SchoolRankLevelSheets extends SheetGenerator {
    @Autowired
    SchoolRankLevelAnalysis schoolRankLevelAnalysis;

    @Autowired
    ProjectConfigService projectConfigService;

    @Autowired
    ClassRankLevelAnalysis classRankLevelAnalysis;

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {
        Target target = sheetTask.get("target");
        String subjectId = target.match(Target.PROJECT) ? null : target.getId().toString();

        Range schoolRange = sheetTask.getRange();

        Param param = new Param().setParameter("projectId", projectId).
                setParameter("subjectId", subjectId).
                setParameter("schoolId", schoolRange.getId());

        List<String> rankLevelParam = projectConfigService.getRankLevelParams(projectId, subjectId);

/*        System.out.println("排序前-->" + rankLevelParam.toString());
        Collections.sort(rankLevelParam, (String s1, String s2) -> s1.compareTo(s2));
        System.out.println("排序后-->" + rankLevelParam.toString());*/

        Result result = schoolRankLevelAnalysis.execute(param);

        //表格列头从project_config表中获取
        setHeader(excelWriter, rankLevelParam);
        setSecondaryHeader(excelWriter, rankLevelParam);
        fillSchoolData(excelWriter, result, rankLevelParam);
        fillClassData(excelWriter, result, rankLevelParam);
    }

    private void setHeader(ExcelWriter excelWriter, List<String> rankLevelParam) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(0, column.incrementAndGet(), "班级名称");
        for (String rankLevel : rankLevelParam) {
            excelWriter.set(0, column.incrementAndGet(), rankLevel);
            excelWriter.mergeCells(0, column.get(), 0, column.incrementAndGet());
        }
    }

    private void setSecondaryHeader(ExcelWriter excelWriter, List<String> rankLevelParam) {
        int row = 1;
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(row, column.incrementAndGet(), "班级名称");
        excelWriter.mergeCells(0, column.get(), 1, column.get());
        for (int i = 0; i < rankLevelParam.size(); i++) {
            excelWriter.set(row, column.incrementAndGet(), "人数");
            excelWriter.set(row, column.incrementAndGet(), "占比");
        }
    }

    private void fillSchoolData(ExcelWriter excelWriter, Result result, List<String> rankLevelParam) {
        AtomicInteger column = new AtomicInteger(-1);
        Map<String, Object> schoolMap = result.get("school");
        List<Map<String, Object>> rankLevels = (List<Map<String, Object>>) schoolMap.get("rankLevels");
        excelWriter.set(2, column.incrementAndGet(), "本校");
        for (String param : rankLevelParam) {
            String rankLevelString = RankLevelFormater.format(param);
            for (Map<String, Object> rankLevel : rankLevels) {
                if (rankLevel.get("rankLevel").equals(rankLevelString)) {
                    excelWriter.set(2, column.incrementAndGet(), rankLevel.get("count") == null ? 0 : rankLevel.get("count"));
                    excelWriter.set(2, column.incrementAndGet(), DoubleUtils.toPercent(Double.parseDouble(rankLevel.get("rate").toString())));
                    break;
                }
            }
        }
    }

    private void fillClassData(ExcelWriter excelWriter, Result result, List<String> rankLevelParam) {
        int row = 3;
        AtomicInteger column = new AtomicInteger(-1);
        List<Map<String, Object>> classList = result.get("classes");
        for (Map<String, Object> clazz : classList) {
            List<Map<String, Object>> rankLevels = (List<Map<String, Object>>) clazz.get("rankLevels");
            excelWriter.set(row, column.incrementAndGet(), clazz.get("className"));
            for (String param : rankLevelParam) {
                String rankLevelString = RankLevelFormater.format(param);
                for (Map<String, Object> rankLevel : rankLevels) {
                    if (rankLevel.get("rankLevel").equals(rankLevelString)) {
                        excelWriter.set(row, column.incrementAndGet(), rankLevel.get("count") == null ? 0 : rankLevel.get("count"));
                        excelWriter.set(row, column.incrementAndGet(), DoubleUtils.toPercent(Double.parseDouble(rankLevel.get("rate").toString())));
                        break;
                    }
                }
            }
            row++;
            column.set(-1);
        }
    }

}
