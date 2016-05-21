package com.xz.controllers;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.bean.ProjectConfig;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.services.*;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.xz.services.SubjectService.getSubjectName;

/**
 * (description)
 * created at 16/05/21
 *
 * @author yiding_he
 */
@Controller
@RequestMapping("/report")
public class GenerateRankLevelReportController {

    @Autowired
    RangeService rangeService;

    @Autowired
    TargetService targetService;

    @Autowired
    SubjectService subjectService;

    @Autowired
    StudentService studentService;

    @Autowired
    ClassService classService;

    @Autowired
    RankLevelService rankLevelService;

    @Autowired
    ProjectConfigService projectConfigService;

    @Autowired
    ScoreService scoreService;

    @RequestMapping("/rank_level")
    @ResponseBody
    public Result generate(@RequestParam("project") String projectId) {
        List<Range> students = rangeService.queryRanges(projectId, Range.STUDENT);
        List<Target> subjects = targetService.queryTargets(projectId, Target.SUBJECT);

        ProjectConfig projectConfig = projectConfigService.getProjectConfig(projectId);
        if (projectConfig.isCombineCategorySubjects()) {
            subjects.add(Target.subject("004005006"));
            subjects.add(Target.subject("007008009"));
        }

        //////////////////////////////////////////////////////////////

        AtomicInteger column = new AtomicInteger(-1);
        ExcelWriter excelWriter = new ExcelWriter();
        excelWriter.set(0, column.incrementAndGet(), "学生");
        excelWriter.set(0, column.incrementAndGet(), "班级");

        for (Target subject : subjects) {
            excelWriter.set(0, column.incrementAndGet(), getSubjectName(subject.getId().toString()));
            excelWriter.set(0, column.incrementAndGet(), "等第");
        }

        excelWriter.set(0, column.incrementAndGet(), "总分");
        excelWriter.set(0, column.incrementAndGet(), "等第");

        //////////////////////////////////////////////////////////////

        AtomicInteger row = new AtomicInteger(1);
        for (Range s : students) {

            String studentId = s.getId();
            Document student = studentService.findStudent(projectId, studentId);
            String classId = student.getString("class");
            String className = classService.findClass(projectId, classId).getString("name");
            column = new AtomicInteger(-1);

            excelWriter.set(row.get(), column.incrementAndGet(), student.getString("name"));
            excelWriter.set(row.get(), column.incrementAndGet(), className);

            for (Target subject : subjects) {
                String subjectId = subject.getId().toString();
                double score = scoreService.getScore(projectId, Range.student(studentId), Target.subject(subjectId));
                String rankLevel = rankLevelService.getRankLevel(projectId, studentId, Target.subject(subjectId), Range.SCHOOL);
                excelWriter.set(row.get(), column.incrementAndGet(), score);
                excelWriter.set(row.get(), column.incrementAndGet(), rankLevel);
            }

            double totalScore = scoreService.getScore(projectId, Range.student(studentId), Target.project(projectId));
            String totalRankLevel = rankLevelService.getRankLevel(projectId, studentId, Target.project(projectId), Range.SCHOOL);
            excelWriter.set(row.get(), column.incrementAndGet(), totalScore);
            excelWriter.set(row.get(), column.incrementAndGet(), totalRankLevel);

            row.incrementAndGet();
        }

        //////////////////////////////////////////////////////////////

        excelWriter.save("target/report.xlsx");
        return Result.success();
    }
}
