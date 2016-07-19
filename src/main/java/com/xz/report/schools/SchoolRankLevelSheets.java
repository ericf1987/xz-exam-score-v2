package com.xz.report.schools;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.api.server.school.SchoolRankLevelAnalysis;
import com.xz.bean.ProjectConfig;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.report.SheetGenerator;
import com.xz.report.SheetTask;
import com.xz.services.ProjectConfigService;
import com.xz.util.DoubleUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

/**
 * @author by fengye on 2016/7/18.
 */
@SuppressWarnings("unchecked")
@Component
public class SchoolRankLevelSheets extends SheetGenerator {
    @Autowired
    SchoolRankLevelAnalysis schoolRankLevelAnalysis;

    @Autowired
    ProjectConfigService projectConfigService;

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {
        Target target = sheetTask.get("target");
        String subjectId = target.match(Target.PROJECT) ? null : target.getId().toString();

        Range schoolRange = sheetTask.getRange();

        Param param = new Param().setParameter("projectId", projectId).
                setParameter("subjectId", subjectId).
                setParameter("schoolId", schoolRange.getId());

        List<String> rankLevelParam = getRankLevelParamList(projectId, subjectId);

/*        System.out.println("排序前-->" + rankLevelParam.toString());
        Collections.sort(rankLevelParam, (String s1, String s2) -> s1.compareTo(s2));
        System.out.println("排序后-->" + rankLevelParam.toString());*/

        Result result = schoolRankLevelAnalysis.execute(param);

        System.out.println(result.getData());
        //获取考试等级参数

        setHeader(excelWriter, rankLevelParam);
        setSecondaryHeader(excelWriter, rankLevelParam);
        fillSchoolData(excelWriter, result, rankLevelParam);
        fillClassData(excelWriter, result, rankLevelParam);
    }

    private List<String> getRankLevelParamList(String projectId, String subjectId) {
        ProjectConfig projectConfig = projectConfigService.getProjectConfig(projectId);
        Map<String, Double> rankLevels = projectConfig.getRankLevels();

        Iterator<String> it = rankLevels.keySet().iterator();

        List<String> rankLevelParam = new ArrayList<>();
        while (it.hasNext()) {
            rankLevelParam.add(it.next());
        }

        return subjectId == null ? projectConfig.getDisplayOptions() : rankLevelParam;
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
        int studentCount = Integer.parseInt(schoolMap.get("studentCount").toString());
        List<Map<String, Object>> rankLevels = (List<Map<String, Object>>) schoolMap.get("rankLevels");
        excelWriter.set(2, column.incrementAndGet(), "本校");
        for (String param : rankLevelParam) {
            String rankLevelString = format(param);
            for (Map<String, Object> rankLevel : rankLevels) {
                if (rankLevel.get("rankLevel").equals(rankLevelString)) {
                    excelWriter.set(2, column.incrementAndGet(), rankLevel.get("count") == null ? 0 : rankLevel.get("count"));
                    excelWriter.set(2, column.incrementAndGet(), getRate(Integer.parseInt(rankLevel.get("count").toString()), studentCount));
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
            int studentCount = Integer.parseInt(clazz.get("studentCount").toString());
            List<Map<String, Object>> rankLevels = (List<Map<String, Object>>) clazz.get("rankLevels");
            excelWriter.set(row, column.incrementAndGet(), clazz.get("className"));
            for (String param : rankLevelParam) {
                String rankLevelString = format(param);
                for (Map<String, Object> rankLevel : rankLevels) {
                    if (rankLevel.get("rankLevel").equals(rankLevelString)) {
                        excelWriter.set(row, column.incrementAndGet(), rankLevel.get("count") == null ? 0 : rankLevel.get("count"));
                        excelWriter.set(row, column.incrementAndGet(), getRate(Integer.parseInt(rankLevel.get("count").toString()), studentCount));
                        break;
                    }
                }
            }
            row++;
            column.set(-1);
        }
    }

    private String getRate(int count, int studentCount) {
        if (studentCount == 0) {
            return "0%";
        }
        double rate = (double) count / (double) studentCount;
        return DoubleUtils.toPercent(rate);
    }

    public static String format(String str) {
        StringBuilder builder = new StringBuilder();

        //匹配数字
        Pattern p_number = Pattern.compile("\\d+");

        //匹配字母
        Pattern p_char = Pattern.compile("[a-zA-Z]+");
        String[] numbers = p_char.split(str);
        String[] chars = p_number.split(str);

        if (numbers.length == 0) {
            return str;
        } else {
            for (int i = 0; i < numbers.length; i++) {
                int pos = Integer.parseInt(numbers[i]);
                for (int j = 0; j < pos; j++) {
                    builder.append(chars[i + 1]);
                }
            }
            return builder.toString();
        }
    }
}
