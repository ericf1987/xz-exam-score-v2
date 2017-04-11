package com.xz.examscore.asynccomponents.report.customization.examAlliance;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.server.customization.examAlliance.StudentQuestScoreRateAnalysis;
import com.xz.examscore.asynccomponents.report.SheetGenerator;
import com.xz.examscore.asynccomponents.report.SheetTask;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author by fengye on 2017/4/10.
 */
@Component
public class StudentQuestScoreRateSheets extends SheetGenerator {

    @Autowired
    StudentQuestScoreRateAnalysis studentQuestScoreRateAnalysis;

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {
        Target target = sheetTask.get("target");
        String subjectId = target.getId().toString();
        Range schoolRange = sheetTask.getRange();
        Param param = new Param().setParameter("projectId", projectId).
                setParameter("schoolId", schoolRange.getId()).
                setParameter("subjectId", subjectId);

        Result result = studentQuestScoreRateAnalysis.execute(param);
        setupHeader(excelWriter, result);
        fillData(excelWriter, result);
    }

    private void setupHeader(ExcelWriter excelWriter, Result result) {
        List<Map<String, Object>> questList = result.get("questList");
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(0, column.incrementAndGet(), "考号");
        excelWriter.set(0, column.incrementAndGet(), "学校考号");
        excelWriter.set(0, column.incrementAndGet(), "学校");
        excelWriter.set(0, column.incrementAndGet(), "班级");
        excelWriter.set(0, column.incrementAndGet(), "姓名");
        for(Map<String, Object> questMap : questList){
            excelWriter.set(0, column.incrementAndGet(), getQuestDesc(questMap));
        }
    }

    public String getQuestDesc(Map<String, Object> questMap){
        String questNo = MapUtils.getString(questMap, "questNo");
        boolean isObjective = BooleanUtils.toBoolean(MapUtils.getBoolean(questMap, "isObjective"));
        return isObjective ? "客观题" + questNo : "主观题" + questNo;
    }

    private void fillData(ExcelWriter excelWriter, Result result) {
        List<Map<String, Object>> studentList = result.get("studentList");
        int row = 1;
        AtomicInteger column = new AtomicInteger(-1);
        for(Map<String, Object> studentMap : studentList){
            excelWriter.set(row, column.incrementAndGet(), MapUtils.getString(studentMap, "examNo"));
            excelWriter.set(row, column.incrementAndGet(), MapUtils.getString(studentMap, "customExamNo"));
            excelWriter.set(row, column.incrementAndGet(), MapUtils.getString(studentMap, "schoolName"));
            excelWriter.set(row, column.incrementAndGet(), MapUtils.getString(studentMap, "className"));
            excelWriter.set(row, column.incrementAndGet(), MapUtils.getString(studentMap, "studentName"));
            List<Map<String, Object>> quests = (List<Map<String, Object>>)studentMap.get("quests");
            for(Map<String, Object> questMap : quests){
                excelWriter.set(row, column.incrementAndGet(), MapUtils.getDouble(questMap, "rate"));
            }
            row++;
            column.set(-1);
        }
    }
}
