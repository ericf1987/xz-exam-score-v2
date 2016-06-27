package com.xz.report.total;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.api.server.project.ProjectTopStudentQuestTypeStat;
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
 * @author by fengye on 2016/6/19.
 * 总体成绩分析-尖子生情况-试卷情况分析
 */
@SuppressWarnings("unchecked")
@Component
public class TotalTopStudentQuestTypeSheets extends SheetGenerator {

    @Autowired
    ProjectTopStudentQuestTypeStat projectTopStudentQuestTypeStat;

    @Autowired
    SchoolService schoolService;

    @Autowired
    TopStudentListService topStudentListService;

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {
        Target subjectTarget = sheetTask.get("target");
        Document doc = topStudentListService.getTopStudentLastOne(projectId, sheetTask.getRange(), sheetTask.getTarget());
        String[] rankSegment = new String[]{"1", doc.get("rank").toString()};
        Param param = new Param().setParameter("projectId", projectId)
                .setParameter("subjectId", subjectTarget.getId().toString())
                .setParameter("rankSegment", rankSegment);
        Result result = projectTopStudentQuestTypeStat.execute(param);
        setupHeader(excelWriter, result);
        fillData(excelWriter, result);
    }

    private void setupHeader(ExcelWriter excelWriter, Result result) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(0, column.incrementAndGet(), "尖子生");
        excelWriter.set(0, column.incrementAndGet(), "所属学校");
        excelWriter.set(0, column.incrementAndGet(), "总体排名");
        List<Map<String, Object>> totals = result.get("totals");
        for(Map<String, Object> total : totals){
            excelWriter.set(0, column.incrementAndGet(), total.get("name"));
        }
    }

    private void fillData(ExcelWriter excelWriter, Result result) {
        int row = 1;
        AtomicInteger column = new AtomicInteger(-1);
        List<Map<String, Object>> topStudents = result.get("topStudents");
        for(Map<String, Object> topStudent : topStudents){
            excelWriter.set(row, column.incrementAndGet(), topStudent.get("name"));
            excelWriter.set(row, column.incrementAndGet(), topStudent.get("schoolName"));
            excelWriter.set(row, column.incrementAndGet(), topStudent.get("rank"));
            List<Map<String, Object>> questTypes = (List<Map<String, Object>>)topStudent.get("questTypes");
            for (Map<String, Object> questType : questTypes){
                excelWriter.set(row, column.incrementAndGet(), DoubleUtils.toPercent(Double.parseDouble(questType.get("scoreRate").toString())));
            }
            row++;
            column.set(-1);
        }
    }
}
