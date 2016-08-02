package com.xz.report.schools;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.api.server.school.SchoolTopStudentQuestTypeStat;
import com.xz.bean.Target;
import com.xz.report.SheetGenerator;
import com.xz.report.SheetTask;
import com.xz.services.SchoolService;
import com.xz.services.TopStudentListService;
import com.xz.util.DoubleUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author by fengye on 2016/6/27.
 * 学校成绩分析-尖子生情况-尖子生试卷情况分析
 */
@SuppressWarnings("unchecked")
@Component
public class SchoolTopStudentQuestTypeSheets extends SheetGenerator {
    @Autowired
    SchoolTopStudentQuestTypeStat schoolTopStudentQuestTypeStat;

    @Autowired
    SchoolService schoolService;

    @Autowired
    TopStudentListService topStudentListService;

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {
        Target subjectTarget = sheetTask.get("target");
        Document doc = topStudentListService.getTopStudentLastOne(projectId, sheetTask.getRange(), sheetTask.getTarget());
        String[] rankSegment = new String[]{"1", doc.get("rank").toString()};
        Param param = new Param()
                .setParameter("projectId", projectId)
                .setParameter("schoolId", sheetTask.getRange().getId())
                .setParameter("subjectId", subjectTarget.getId().toString())
                .setParameter("rankSegment", rankSegment);
        Result result = schoolTopStudentQuestTypeStat.execute(param);
        setupHeader(excelWriter, result);
        setupStuData(excelWriter, result);
    }

    private void setupHeader(ExcelWriter excelWriter, Result result) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(0, column.incrementAndGet(), "尖子生");
        excelWriter.set(0, column.incrementAndGet(), "所属班级");
        excelWriter.set(0, column.incrementAndGet(), "总体排名");
        List<Map<String, Object>> schools = result.get("schools");
        for (Map<String, Object> school : schools) {
            excelWriter.set(0, column.incrementAndGet(), school.get("name"));
        }
    }

    private void setupStuData(ExcelWriter excelWriter, Result result) {
        int row = 1;
        AtomicInteger column = new AtomicInteger(-1);
        List<Map<String, Object>> topStudents = result.get("topStudents");
        for(Map<String, Object> topStudent : topStudents){
            List<Map<String, Object>> questTypes = (List<Map<String, Object>>)topStudent.get("questTypes");
            excelWriter.set(row, column.incrementAndGet(), topStudent.get("name"));
            excelWriter.set(row, column.incrementAndGet(), topStudent.get("className"));
            excelWriter.set(row, column.incrementAndGet(), topStudent.get("rank"));
            for(Map<String, Object> questType : questTypes){
                //excelWriter.set(row, column.incrementAndGet(), DoubleUtils.toPercent(Double.valueOf(questType.get("scoreRate").toString())));
                excelWriter.set(row, column.incrementAndGet(), questType.get("score"));
            }
            row++;
            column.set(-1);
        }
    }
}
