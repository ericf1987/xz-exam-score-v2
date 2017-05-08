package com.xz.examscore.asynccomponents.report.customization.examAlliance;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.ajia.Param;
import com.xz.examscore.api.server.customization.examAlliance.TotalQuestAbilityLevelAnalysis;
import com.xz.examscore.asynccomponents.report.SheetGenerator;
import com.xz.examscore.asynccomponents.report.SheetTask;
import com.xz.examscore.services.SubjectService;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author by fengye on 2017/1/5.
 */
@Component
public class TotalQuestAbilityLevelScoreSheets extends SheetGenerator {

    @Autowired
    TotalQuestAbilityLevelAnalysis totalQuestAbilityLevelAnalysis;

    @Autowired
    SubjectService subjectService;

    public static final String[] SECONDARY_HEADER = new String[]{
            "水平检测", "能力检测", "总分"
    };

    public static final String[] THIRTH_HEADER = new String[]{
            "人平均分", "指标率"
    };

    public static final String[] LAST_HEADER = new String[]{
            "总分", "指标率"
    };

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {
        Param param = new Param().setParameter("projectId", projectId);
        Result result = totalQuestAbilityLevelAnalysis.execute(param);
        List<Map<String, Object>> totalQuestAbilityLevel = result.get("totalQuestAbilityLevel");
        List<String> subjectIds = subjectService.querySubjects(projectId);
        setHeader(excelWriter, subjectIds);
        setSecondaryHeader(excelWriter, subjectIds);
        setThirthHeader(excelWriter, subjectIds);
        fillData(excelWriter, subjectIds, totalQuestAbilityLevel);
    }

    private void setHeader(ExcelWriter excelWriter, List<String> subjectIds) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(0, column.incrementAndGet(), "学校");
        excelWriter.set(0, column.incrementAndGet(), "参考人数");
        int length = SECONDARY_HEADER.length * THIRTH_HEADER.length;
        for (String subjectId : subjectIds) {
            for (int i = 0; i < length; i++) {
                excelWriter.set(0, column.incrementAndGet(), SubjectService.getSubjectName(subjectId));
            }
            excelWriter.mergeCells(0, column.get() - length + 1, 0, column.get());
        }
        for (int j = 0; j < length; j++) {
            excelWriter.set(0, column.incrementAndGet(), "总分");
        }
        excelWriter.mergeCells(0, column.get() - length + 1, 0, column.get());
    }

    private void setSecondaryHeader(ExcelWriter excelWriter, List<String> subjectIds) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(1, column.incrementAndGet(), "学校");
        excelWriter.set(1, column.incrementAndGet(), "参考人数");
        for (String subjectId : subjectIds) {
            String subjectName = SubjectService.getSubjectName(subjectId);
            for (String c : SECONDARY_HEADER) {
                for (int i = 0; i < THIRTH_HEADER.length; i++) {
                    excelWriter.set(1, column.incrementAndGet(), c.equals("总分") ? subjectName + c : c);
                }
                excelWriter.mergeCells(1, column.get() - 1, 1, column.get());
            }
        }
        for (String c : SECONDARY_HEADER) {
            for (int j = 0; j < LAST_HEADER.length; j++) {
                excelWriter.set(1, column.incrementAndGet(), c);
            }
            excelWriter.mergeCells(1, column.get() - 1, 1, column.get());
        }
    }

    private void setThirthHeader(ExcelWriter excelWriter, List<String> subjectIds) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(2, column.incrementAndGet(), "学校");
        excelWriter.mergeCells(0, 0, 2, 0);
        excelWriter.set(2, column.incrementAndGet(), "参考人数");
        excelWriter.mergeCells(0, 1, 2, 1);

        for (int i = 0; i < subjectIds.size(); i++) {
            for (int j = 0; j < SECONDARY_HEADER.length; j++) {
                for (String t : THIRTH_HEADER) {
                    excelWriter.set(2, column.incrementAndGet(), t);
                }
            }
        }

        for (String c : SECONDARY_HEADER) {
            for (String t : LAST_HEADER) {
                excelWriter.set(2, column.incrementAndGet(), t);
            }
        }
    }

    private void fillData(ExcelWriter excelWriter, List<String> subjectIds, List<Map<String, Object>> totalQuestAbilityLevel) {
        AtomicInteger column = new AtomicInteger(-1);
        int row = 3;
        for (Map<String, Object> map : totalQuestAbilityLevel) {
            excelWriter.set(row, column.incrementAndGet(), map.get("schoolName"));
            excelWriter.set(row, column.incrementAndGet(), map.get("studentCount"));
            List<Map<String, Object>> subjects = (List<Map<String, Object>>) map.get("subjects");
            for (String subjectId : subjectIds) {

                Map<String, Object> m = subjects.stream().filter(subject -> subject.get("subjectId").equals(subjectId)).collect(Collectors.toList()).get(0);

                Map<String, Object> level = (Map<String, Object>) m.get("level");
                excelWriter.set(row, column.incrementAndGet(), level.get("average"));
                excelWriter.set(row, column.incrementAndGet(), level.get("rate"));

                Map<String, Object> ability = (Map<String, Object>) m.get("ability");
                excelWriter.set(row, column.incrementAndGet(), ability.get("average"));
                excelWriter.set(row, column.incrementAndGet(), ability.get("rate"));

                Map<String, Object> total = (Map<String, Object>) m.get("total");
                excelWriter.set(row, column.incrementAndGet(), total.get("average"));
                excelWriter.set(row, column.incrementAndGet(), total.get("rate"));
            }

            Map<String, Object> total = (Map<String, Object>) map.get("total");
            Map<String, Object> l = (Map<String, Object>) total.get("level");
            Map<String, Object> a = (Map<String, Object>) total.get("ability");
            Map<String, Object> t = (Map<String, Object>) total.get("total");

            excelWriter.set(row, column.incrementAndGet(), MapUtils.getDouble(l, "score"));
            excelWriter.set(row, column.incrementAndGet(), MapUtils.getDouble(l, "rate"));
            excelWriter.set(row, column.incrementAndGet(), MapUtils.getDouble(a, "score"));
            excelWriter.set(row, column.incrementAndGet(), MapUtils.getDouble(a, "rate"));
            excelWriter.set(row, column.incrementAndGet(), MapUtils.getDouble(t, "score"));
            excelWriter.set(row, column.incrementAndGet(), MapUtils.getDouble(t, "rate"));

            row++;
            column.set(-1);
        }
    }
}
