package com.xz.examscore.api.server.classes;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.api.server.school.SchoolRankLevelAnalysis;
import com.xz.examscore.bean.ProjectConfig;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.*;
import com.xz.examscore.util.RankLevelFormater;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.collections.MapUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author by fengye on 2016/12/20.
 */
@Function(description = "班级成绩-等第统计分析", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "classId", type = Type.String, description = "班级Id", required = false)
})
@Service
public class ClassCombinedRankLevelAnalysis implements Server {

    @Autowired
    TargetService targetService;

    @Autowired
    StudentService studentService;

    @Autowired
    RankLevelService rankLevelService;

    @Autowired
    ProjectConfigService projectConfigService;

    @Autowired
    ScoreService scoreService;

    @Autowired
    SubjectService subjectService;

    @Autowired
    SubjectCombinationService subjectCombinationService;

    @Autowired
    FullScoreService fullScoreService;

    @Autowired
    SchoolRankLevelAnalysis scholRankLevelAnalysis;

    @Autowired
    ClassService classService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String classId = param.getString("classId");

        ProjectConfig projectConfig = projectConfigService.getProjectConfig(projectId);
        String lastRankLevel = projectConfig.getLastRankLevel();
        List<Map<String, Object>> students = new ArrayList<>();

        //非组合科目
        List<String> nonCombinedSubjectIds = new ArrayList<>(subjectService.querySubjects(projectId)).stream().filter(
                subjectId -> !StringUtil.isOneOf(subjectId, "004", "005", "006", "007", "008", "009", "004005006", "007008009")).collect(Collectors.toList());
        //组合科目
        List<String> combinedSubjectIds = new ArrayList<>(subjectCombinationService.getAllSubjectCombinations(projectId));

        processRankLevelAnalysis(projectId, Range.clazz(classId), lastRankLevel, students,
                nonCombinedSubjectIds, combinedSubjectIds, Range.CLASS);

        return Result.success().set("subjectIds", ListUtils.union(nonCombinedSubjectIds, combinedSubjectIds))
                .set("students", students);
    }

    public void processRankLevelAnalysis(
            String projectId, Range range, String lastRankLevel, List<Map<String, Object>> students,
            List<String> nonCombinedSubjectIds, List<String> combinedSubjectIds, String rangeName) {
        List<Document> studentList = studentService.getStudentList(projectId, range);

        for (Document studentDoc : studentList) {
            Map<String, Object> studentMap = new HashMap<>();
            StringBuilder totalRankLevel = new StringBuilder();
            List<Map<String, Object>> subjects = new ArrayList<>();
            String studentName = studentDoc.getString("name");
            String examNo = studentDoc.getString("examNo");
            String customExamNo = studentDoc.getString("customExamNo");
            Range studentRange = Range.student(studentDoc.getString("student"));
            for (String subject : nonCombinedSubjectIds) {
                Target target = Target.subject(subject);
                Map<String, Object> subjectRankLevelMap = getSubjectRankLevelMap(projectId, lastRankLevel, studentDoc, totalRankLevel, studentRange, target, rangeName);
                subjects.add(subjectRankLevelMap);
            }
            for (String combinedSubject : combinedSubjectIds) {
                Target target = Target.subjectCombination(combinedSubject);
                Map<String, Object> subjectRankLevelMap = getSubjectRankLevelMap(projectId, lastRankLevel, studentDoc, totalRankLevel, studentRange, target, rangeName);
                subjects.add(subjectRankLevelMap);
            }
            double totalScore = subjects.stream().mapToDouble(subjectMap -> MapUtils.getDouble(subjectMap, "score")).sum();
            studentMap.put("studentName", studentName);
            studentMap.put("examNo", examNo);
            studentMap.put("customExamNo", customExamNo);
            if (rangeName.equals(Range.SCHOOL)) {
                studentMap.put("className", classService.getClassName(projectId, studentDoc.getString("class")));
            }
            studentMap.put("subjects", subjects);
            studentMap.put("totalRankLevel", RankLevelFormater.format2(totalRankLevel.toString().trim()));
            studentMap.put("totalScore", totalScore);
            students.add(studentMap);
        }
        Collections.sort(students, (Map<String, Object> m1, Map<String, Object> m2) -> {
            Double d1 = MapUtils.getDouble(m1, "totalScore");
            Double d2 = MapUtils.getDouble(m2, "totalScore");
            return d2.compareTo(d1);
        });
    }

    public Map<String, Object> getSubjectRankLevelMap(
            String projectId, String lastRankLevel, Document studentDoc, StringBuilder totalRankLevel,
            Range studentRange, Target target, String rangeName) {
        Map<String, Object> subjectMap = new HashMap<>();
        String subjectName = SubjectService.getSubjectName(target.getId().toString());
        double score = scoreService.getScore(projectId, studentRange, target);
        String rankLevel = rankLevelService.getRankLevel(projectId, studentDoc.getString("student"), target, rangeName, lastRankLevel);
        totalRankLevel.append(rankLevel);
        subjectMap.put("subjectId", target.getId().toString());
        subjectMap.put("subjectName", subjectName);
        subjectMap.put("score", score);
        subjectMap.put("rankLevel", rankLevel);
        return subjectMap;
    }
}
