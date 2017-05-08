package com.xz.examscore.asynccomponents.report.total;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.ajia.Param;
import com.xz.examscore.api.server.project.ProjectTopStudentStat;
import com.xz.examscore.asynccomponents.report.SheetGenerator;
import com.xz.examscore.asynccomponents.report.SheetTask;
import com.xz.examscore.services.SchoolService;
import com.xz.examscore.services.TopStudentListService;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author by fengye on 2016/6/19.
 * 总体成绩分析-尖子生情况-尖子生统计
 */
@SuppressWarnings("unchecked")
@Component
public class TotalTopStudentSheets extends SheetGenerator {

    @Autowired
    ProjectTopStudentStat projectTopStudentStat;

    @Autowired
    SchoolService schoolService;

    @Autowired
    TopStudentListService topStudentListService;

    public static final String[] SECONDARY_COLUMN = new String[]{
            "成绩", "总体排名"
    };

    static final Logger LOG = LoggerFactory.getLogger(TotalTopStudentSheets.class);

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {
        //查询尖子生表总人数
        Document doc = topStudentListService.getTopStudentLastOne(projectId, sheetTask.getRange(), sheetTask.getTarget());
        if(null == doc || doc.isEmpty()){
            LOG.error("找不到尖子生信息, project={}, range={}, target={}", projectId, sheetTask.getRange(), sheetTask.getTarget());
            return;
        }
        String[] rankSegment = new String[]{"1", doc.get("rank").toString()};
        Param param = new Param().setParameter("projectId", projectId).setParameter("rankSegment", rankSegment);
        Result result = projectTopStudentStat.execute(param);
        setupHeader(excelWriter, result.get("topStudents"));
        setupSecondaryHeader(excelWriter, result.get("topStudents"));
        fillData(excelWriter, result.get("topStudents"));
    }

    private void setupHeader(ExcelWriter excelWriter, List<Map<String, Object>> topStudents) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(0, column.incrementAndGet(), "考号");
        excelWriter.set(0, column.incrementAndGet(), "学校考号");
        excelWriter.set(0, column.incrementAndGet(), "学生姓名");
        excelWriter.set(0, column.incrementAndGet(), "学校");
        excelWriter.set(0, column.incrementAndGet(), "总体排名");
        appendHeader(excelWriter, topStudents, column);
    }

    public void appendHeader(ExcelWriter excelWriter, List<Map<String, Object>> topStudents, AtomicInteger column) {
        if(null != topStudents && !topStudents.isEmpty()){
            Map<String, Object> topStudent = topStudents.get(0);
            List<Map<String, Object>> subjects = (List<Map<String, Object>>) topStudent.get("subjects");
            for (Map<String, Object> subject : subjects) {
                excelWriter.set(0, column.incrementAndGet(), subject.get("subjectName"));
                excelWriter.mergeCells(0, column.get(), 0, column.incrementAndGet());
            }
        }
    }

    private void setupSecondaryHeader(ExcelWriter excelWriter, List<Map<String, Object>> topStudents) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(1, column.incrementAndGet(), "考号");
        excelWriter.mergeCells(0, column.get(), 1, column.get());
        excelWriter.set(1, column.incrementAndGet(), "学校考号");
        excelWriter.mergeCells(0, column.get(), 1, column.get());
        excelWriter.set(1, column.incrementAndGet(), "学生姓名");
        excelWriter.mergeCells(0, column.get(), 1, column.get());
        excelWriter.set(1, column.incrementAndGet(), "学校");
        excelWriter.mergeCells(0, column.get(), 1, column.get());
        excelWriter.set(1, column.incrementAndGet(), "总体排名");
        excelWriter.mergeCells(0, column.get(), 1, column.get());
        appendSecondaryHeader(excelWriter, topStudents, column);
    }

    public void appendSecondaryHeader(ExcelWriter excelWriter, List<Map<String, Object>> topStudents, AtomicInteger column) {
        if(null != topStudents && !topStudents.isEmpty()){
            Map<String, Object> topStudent = topStudents.get(0);
            List<Map<String, Object>> subjects = (List<Map<String, Object>>) topStudent.get("subjects");
            for (int i = 0; i < subjects.size(); i++) {
                excelWriter.set(1, column.incrementAndGet(), SECONDARY_COLUMN[0]);
                excelWriter.set(1, column.incrementAndGet(), SECONDARY_COLUMN[1]);
            }
        }
    }

    private void fillData(ExcelWriter excelWriter, List<Map<String, Object>> topStudents) {
        int row = 2;
        AtomicInteger column = new AtomicInteger(-1);
        for (Map<String, Object> topStudent : topStudents) {
            excelWriter.set(row, column.incrementAndGet(), topStudent.get("examNo"));
            excelWriter.set(row, column.incrementAndGet(), topStudent.get("customExamNo"));
            excelWriter.set(row, column.incrementAndGet(), topStudent.get("name"));
            excelWriter.set(row, column.incrementAndGet(), topStudent.get("schoolName"));
            excelWriter.set(row, column.incrementAndGet(), topStudent.get("rank"));
            List<Map<String, Object>> subjects = (List<Map<String, Object>>) topStudent.get("subjects");
            for (Map<String, Object> subject : subjects) {
                excelWriter.set(row, column.incrementAndGet(), subject.get("score"));
                excelWriter.set(row, column.incrementAndGet(), subject.get("rankIndex"));
            }
            row++;
            column.set(-1);
        }
    }


}
