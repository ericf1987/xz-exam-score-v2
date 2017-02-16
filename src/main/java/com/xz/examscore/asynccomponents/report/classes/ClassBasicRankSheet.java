package com.xz.examscore.asynccomponents.report.classes;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.server.classes.ClassRankAnalysis;
import com.xz.examscore.asynccomponents.report.SheetGenerator;
import com.xz.examscore.asynccomponents.report.SheetTask;
import com.xz.examscore.bean.Range;
import com.xz.examscore.services.ClassService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author by fengye on 2016/6/8.
 * 班级成绩分析-基础分析-分数排名统计
 */
@SuppressWarnings("unchecked")
@Component
public class ClassBasicRankSheet extends SheetGenerator {
    @Autowired
    ClassRankAnalysis classRankAnalysis;

    @Autowired
    ClassService classService;

    public static final String[] SECONDARY_HEADER = new String[]{
            "得分", "班级排名", "学校排名"
    };

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {
        Range classRange = sheetTask.getRange();
        Document doc = classService.findClass(projectId, classRange.getId());
        String schoolId = doc.getString("school");

        Param param = new Param().setParameter("projectId", projectId).
                setParameter("schoolId", schoolId).
                setParameter("classId", classRange.getId());

        Result result = classRankAnalysis.execute(param);
        setupHeader(excelWriter, result.get("rankstats"));
        setupSecondaryHeader(excelWriter, result.get("rankstats"));
        fillStudentData(excelWriter, result.get("rankstats"));
    }

    private void fillStudentData(ExcelWriter excelWriter, List<Map<String, Object>> rankstats) {
        int row = 2;
        for (Map<String, Object> rankstat : rankstats) {
            fillRow(row, excelWriter, rankstat);
            row++;
        }
    }

    private void fillRow(int row, ExcelWriter excelWriter, Map<String, Object> rankstat) {
        AtomicInteger column = new AtomicInteger(-1);
        if(null != rankstat && !rankstat.isEmpty()){
            Map<String, Object> projectRank = (Map<String, Object>) rankstat.get("projectRankStat");
            excelWriter.set(row, column.incrementAndGet(), getString(rankstat, "examNo"));
            excelWriter.set(row, column.incrementAndGet(), getString(rankstat, "customExamNo"));
            excelWriter.set(row, column.incrementAndGet(), getString(rankstat, "studentName"));
            excelWriter.set(row, column.incrementAndGet(), projectRank.get("score"));
            Map<String, Object> projectRankStat = (Map<String, Object>)rankstat.get("projectRankStat");
            excelWriter.set(row, column.incrementAndGet(), projectRankStat.get("rankClassIndex"));
            excelWriter.set(row, column.incrementAndGet(), projectRankStat.get("rankSchoolIndex"));
            List<Map<String, Object>> subjectRankStat = (List<Map<String, Object>>) rankstat.get("subjectRankStat");
            for (Map<String, Object> subjectRank : subjectRankStat) {
                excelWriter.set(row, column.incrementAndGet(), subjectRank.get("score"));
                excelWriter.set(row, column.incrementAndGet(), subjectRank.get("rankClassIndex"));
                excelWriter.set(row, column.incrementAndGet(), subjectRank.get("rankSchoolIndex"));
            }
        }
    }

    private String getString(Map<String, Object> map, String property) {
        Object value = map.get(property);
        return value == null ? "" : value.toString();
    }

    private void setupHeader(ExcelWriter excelWriter, List<Map<String, Object>> rankstats) {
        int row = 0;
        if(null != rankstats && !rankstats.isEmpty()){
            Map<String, Object> rankstat = rankstats.get(0);
            List<Map<String, Object>> subjectRankStat = (List<Map<String, Object>>) rankstat.get("subjectRankStat");
            List<String> subjects = new ArrayList<>();
            for (Map<String, Object> subject : subjectRankStat) {
                String subjectName = getString(subject, "subjectName");
                subjects.add(subjectName);
            }
            AtomicInteger column = new AtomicInteger(-1);
            excelWriter.set(row, column.incrementAndGet(), "考号");
            excelWriter.set(row, column.incrementAndGet(), "学校考号");
            excelWriter.set(row, column.incrementAndGet(), "学生姓名");
            excelWriter.set(row, column.incrementAndGet(), "全部科目");
            column.incrementAndGet();
            column.incrementAndGet();
            excelWriter.mergeCells(0, column.get() - 2, 0, column.get());
            for (String subjectName : subjects) {
                excelWriter.set(row, column.incrementAndGet(), subjectName);
                column.incrementAndGet();
                column.incrementAndGet();
                excelWriter.mergeCells(0, column.get() - 2, 0, column.get());
            }
        }
    }

    private void setupSecondaryHeader(ExcelWriter excelWriter, List<Map<String, Object>> rankstats) {
        int row = 1;
        if(null != rankstats && !rankstats.isEmpty()){
            Map<String, Object> rankstat = rankstats.get(0);
            List<Map<String, Object>> subjectRankStat = (List<Map<String, Object>>) rankstat.get("subjectRankStat");
            List<String> subjects = new ArrayList<>();
            for (Map<String, Object> subject : subjectRankStat) {
                String subjectName = getString(subject, "subjectName");
                subjects.add(subjectName);
            }
            AtomicInteger column = new AtomicInteger(-1);
            excelWriter.set(row, column.incrementAndGet(), "考号");
            excelWriter.mergeCells(0, 0, 1, 0);
            excelWriter.set(row, column.incrementAndGet(), "学校考号");
            excelWriter.mergeCells(0, 1, 1, 1);
            excelWriter.set(row, column.incrementAndGet(), "学生姓名");
            excelWriter.mergeCells(0, 2, 1, 2);
            excelWriter.set(row, column.incrementAndGet(), SECONDARY_HEADER[0]);
            excelWriter.set(row, column.incrementAndGet(), SECONDARY_HEADER[1]);
            excelWriter.set(row, column.incrementAndGet(), SECONDARY_HEADER[2]);
            for (int i = 0; i < subjects.size(); i++) {
                excelWriter.set(row, column.incrementAndGet(), SECONDARY_HEADER[0]);
                excelWriter.set(row, column.incrementAndGet(), SECONDARY_HEADER[1]);
                excelWriter.set(row, column.incrementAndGet(), SECONDARY_HEADER[2]);
            }
        }
    }
}
