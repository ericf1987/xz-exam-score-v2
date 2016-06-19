package com.xz.report.total;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.api.server.project.ProjectTopStudentStat;
import com.xz.report.SheetGenerator;
import com.xz.report.SheetTask;
import com.xz.services.SchoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author by fengye on 2016/6/19.
 */
@Component
public class TotalTopStudentSheets extends SheetGenerator {

    @Autowired
    ProjectTopStudentStat projectTopStudentStat;

    @Autowired
    SchoolService schoolService;

    public static final String[] RANKSEGMENTS = new String[]{
            "1", "50"
    };

    public static final String[] SECONDARY_COLUMN = new String[]{
            "成绩", "总体排名"
    };

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {
        Param param = new Param().setParameter("projectId", projectId).setParameter("rankSegment", RANKSEGMENTS);
        Result result = projectTopStudentStat.execute(param);
        System.out.println("尖子生统计-->" + result.getData());
        setupHeader(excelWriter, result.get("topStudents"));
        setupSecondaryHeader(excelWriter, result.get("topStudents"));
        fillData(excelWriter, result.get("topStudents"));
    }

    private void setupHeader(ExcelWriter excelWriter, List<Map<String, Object>> topStudents) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(0, column.incrementAndGet(), "学生姓名");
        excelWriter.set(0, column.incrementAndGet(), "学校");
        excelWriter.set(0, column.incrementAndGet(), "总体排名");
        Map<String, Object> topStudent = topStudents.get(0);
        List<Map<String, Object>> subjects = (List<Map<String, Object>>) topStudent.get("subjects");
        for (Map<String, Object> subject : subjects) {
            excelWriter.set(0, column.incrementAndGet(), subject.get("subjectName"));
            excelWriter.mergeCells(0, column.get(), 0, column.incrementAndGet());
        }
    }

    private void setupSecondaryHeader(ExcelWriter excelWriter, List<Map<String, Object>> topStudents) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(1, column.incrementAndGet(), "学生姓名");
        excelWriter.mergeCells(0, column.get(), 1, column.get());
        excelWriter.set(1, column.incrementAndGet(), "学校");
        excelWriter.mergeCells(0, column.get(), 1, column.get());
        excelWriter.set(1, column.incrementAndGet(), "总体排名");
        excelWriter.mergeCells(0, column.get(), 1, column.get());
        Map<String, Object> topStudent = topStudents.get(0);
        List<Map<String, Object>> subjects = (List<Map<String, Object>>) topStudent.get("subjects");
        for (int i = 0; i < subjects.size(); i++) {
            excelWriter.set(1, column.incrementAndGet(), SECONDARY_COLUMN[0]);
            excelWriter.set(1, column.incrementAndGet(), SECONDARY_COLUMN[1]);
        }
    }

    private void fillData(ExcelWriter excelWriter, List<Map<String, Object>> topStudents) {
        int row = 2;
        AtomicInteger column = new AtomicInteger(-1);
        for (Map<String, Object> topStudent : topStudents) {
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
