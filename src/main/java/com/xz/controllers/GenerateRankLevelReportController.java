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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;
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

    @RequestMapping(value = "/rank_level", method = RequestMethod.POST)
    @ResponseBody
    public Result generate(@RequestParam("project") String projectId) {
        List<Range> students = rangeService.queryRanges(projectId, Range.STUDENT);
        List<Target> subjects = targetService.queryTargets(projectId, Target.SUBJECT);

        List<SortItem> sortItems = new ArrayList<>();   // 用来做最后的排名

        ProjectConfig projectConfig = projectConfigService.getProjectConfig(projectId);
        String lastRankLevel = projectConfig.getLastRankLevel();
        if (projectConfig.isCombineCategorySubjects()) {
            subjects.add(Target.subject("004005006"));
            subjects.add(Target.subject("007008009"));
        }

        //////////////////////////////////////////////////////////////

        AtomicInteger column = new AtomicInteger(-1);
        ExcelWriter excelWriter = new ExcelWriter();
        excelWriter.set(0, column.incrementAndGet(), "编号");
        excelWriter.set(0, column.incrementAndGet(), "学生");
        excelWriter.set(0, column.incrementAndGet(), "班级");

        for (Target subject : subjects) {
            excelWriter.set(0, column.incrementAndGet(), getSubjectName(subject.getId().toString()));
            excelWriter.set(0, column.incrementAndGet(), "等第");
        }

        excelWriter.set(0, column.incrementAndGet(), "总分");
        excelWriter.set(0, column.incrementAndGet(), "等第");
        excelWriter.set(0, column.incrementAndGet(), "等第排名");

        //////////////////////////////////////////////////////////////

        AtomicInteger row = new AtomicInteger(1);
        for (Range s : students) {

            String studentId = s.getId();
            Document student = studentService.findStudent(projectId, studentId);
            String classId = student.getString("class");
            String className = classService.findClass(projectId, classId).getString("name");
            column = new AtomicInteger(-1);

            excelWriter.set(row.get(), column.incrementAndGet(), studentId);
            excelWriter.set(row.get(), column.incrementAndGet(), student.getString("name"));
            excelWriter.set(row.get(), column.incrementAndGet(), className);

            for (Target subject : subjects) {
                String subjectId = subject.getId().toString();
                double score = scoreService.getScore(projectId, Range.student(studentId), Target.subject(subjectId));
                String rankLevel = rankLevelService.getRankLevel(
                        projectId, studentId, Target.subject(subjectId), Range.SCHOOL, lastRankLevel);

                excelWriter.set(row.get(), column.incrementAndGet(), score);
                excelWriter.set(row.get(), column.incrementAndGet(), rankLevel);
            }

            double totalScore = scoreService.getScore(projectId, Range.student(studentId), Target.project(projectId));
            String totalRankLevel = rankLevelService.getRankLevel(
                    projectId, studentId, Target.project(projectId), Range.SCHOOL, lastRankLevel);
            excelWriter.set(row.get(), column.incrementAndGet(), totalScore);
            excelWriter.set(row.get(), column.incrementAndGet(), totalRankLevel);

            sortItems.add(new SortItem(studentId, totalRankLevel, totalScore));

            row.incrementAndGet();
        }

        //////////////////////////////////////////////////////////////

        Collections.sort(sortItems);
        Map<String, SortItem> sortItemMap = new HashMap<>();

        // 1. 计算排名，2. 构造 sortItemMap
        for (int i = 0; i < sortItems.size(); i++) {
            SortItem sortItem = sortItems.get(i);
            sortItemMap.put(sortItem.getStudentId(), sortItem); // 构造 map 给后面的循环用

            if (i > 0) {
                SortItem last = sortItems.get(i - 1);
                if (sortItem.compareTo(last) == 0) {
                    sortItem.setRank(last.rank);
                } else {
                    sortItem.setRank(i + 1);
                }
            } else {
                sortItem.setRank(1);
            }
        }

        int studentSize = students.size();
        int columnIndex = column.incrementAndGet();
        for (int i = 0; i < studentSize; i++) {
            int rowIndex = i + 1;
            String studentId = excelWriter.getString(rowIndex, 0);
            SortItem sortItem = sortItemMap.get(studentId);
            excelWriter.set(rowIndex, columnIndex, sortItem.getRank());
        }

        //////////////////////////////////////////////////////////////

        excelWriter.save("target/rank_level_map/" + projectId + ".xlsx");
        return Result.success();
    }

    //////////////////////////////////////////////////////////////

    // 等第、总分排名
    private static class SortItem implements Comparable<SortItem> {

        private String studentId;

        private String rankLevels;

        private double totalScore;

        private int rank;

        public SortItem(String studentId, String rankLevels, double totalScore) {
            this.studentId = studentId;
            this.rankLevels = rankLevels;
            this.totalScore = totalScore;
        }

        public String getStudentId() {
            return studentId;
        }

        public void setStudentId(String studentId) {
            this.studentId = studentId;
        }

        public String getRankLevels() {
            return rankLevels;
        }

        public void setRankLevels(String rankLevels) {
            this.rankLevels = rankLevels;
        }

        public double getTotalScore() {
            return totalScore;
        }

        public void setTotalScore(double totalScore) {
            this.totalScore = totalScore;
        }

        public int getRank() {
            return rank;
        }

        public void setRank(int rank) {
            this.rank = rank;
        }

        @Override
        public int compareTo(SortItem o) {

            // 排名等级顺序
            int rankLevelCompare = this.rankLevels.compareTo(o.rankLevels);

            if (rankLevelCompare != 0) {
                return rankLevelCompare;
            } else {
                // 分数倒序
                return this.totalScore > o.totalScore ? -1 : (this.totalScore < o.totalScore ? 1 : 0);
            }
        }
    }
}
