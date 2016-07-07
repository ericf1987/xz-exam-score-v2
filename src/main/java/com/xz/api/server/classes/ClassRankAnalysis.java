package com.xz.api.server.classes;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.api.annotation.Function;
import com.xz.api.annotation.Parameter;
import com.xz.api.annotation.Type;
import com.xz.api.server.Server;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.services.*;
import com.xz.util.DoubleUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 班级成绩-排名分析
 *
 * @author zhaorenwu
 */
@SuppressWarnings("unchecked")
@Function(description = "班级成绩-排名分析", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "schoolId", type = Type.String, description = "学校id", required = true),
        @Parameter(name = "classId", type = Type.String, description = "班级id", required = true)
})
@Service
public class ClassRankAnalysis implements Server {

    public static Logger LOG = LoggerFactory.getLogger(ClassRankAnalysis.class);

    @Autowired
    StudentService studentService;

    @Autowired
    SubjectService subjectService;

    @Autowired
    ScoreService scoreService;

    @Autowired
    AverageService averageService;

    @Autowired
    RankService rankService;

    @Autowired
    FullScoreService fullScoreService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String schoolId = param.getString("schoolId");
        String classId = param.getString("classId");

        List<Map<String, Object>> rankstats = new ArrayList<>();
        List<Document> studentList = studentService.getStudentList(projectId, Range.clazz(classId));
        for (Document student : studentList) {
            Map<String, Object> map = new HashMap<>();

            String studentId = student.getString("student");
            String studentName = student.getString("name");
            map.put("studentId", studentId);
            map.put("studentName", studentName);

            // 项目排行分析
            Map<String, Object> projectRankMap = getRankAnalysisMap(
                    projectId, Target.project(projectId), schoolId, classId, studentId);
            map.put("projectRankStat", projectRankMap);

            // 科目排行分析
            List<Map<String, Object>> subjectRankList = getSubjectRankList(projectId, schoolId, classId, studentId);
            map.put("subjectRankStat", subjectRankList);

            rankstats.add(map);
        }

        rankstats.sort((o1, o2) -> {
            Double score1 = (Double) ((Map<String, Object>) o1.get("projectRankStat")).get("score");
            Double score2 = (Double) ((Map<String, Object>) o2.get("projectRankStat")).get("score");
            return score2.compareTo(score1);
        });

        List<String> subjects = subjectService.querySubjects(projectId);
        return Result.success().set("rankstats", rankstats).set("hasHeader", !subjects.isEmpty());
    }

    private List<Map<String, Object>> getSubjectRankList(
            String projectId, String schoolId, String classId, String studentId) {
        List<Map<String, Object>> subjectRankList = new ArrayList<>();

        List<String> subjects = subjectService.querySubjects(projectId);
        for (String subjectId : subjects) {

            Map<String, Object> rankAnalysisMap = getRankAnalysisMap(
                    projectId, Target.subject(subjectId), schoolId, classId, studentId);
            rankAnalysisMap.put("subjectId", subjectId);
            rankAnalysisMap.put("subjectName", SubjectService.getSubjectName(subjectId));
            subjectRankList.add(rankAnalysisMap);
        }

        return subjectRankList;
    }

    // 获取排行分析
    private Map<String, Object> getRankAnalysisMap(
            String projectId, Target target, String schoolId, String classId, String studentId) {
        Map<String, Object> rankMaps = new HashMap<>();

        double fullScore = fullScoreService.getFullScore(projectId, target);

        // 总分
        rankMaps.put("fullScore", fullScore);

        // 学生得分/得分率
        double score = scoreService.getScore(projectId, Range.student(studentId), target);
        rankMaps.put("score", score);
        rankMaps.put("scoreRate", DoubleUtils.round(fullScore == 0 ? 0 : score / fullScore, true));

        // 学生在班级排名
        int rankClassIndex = rankService.getRank(projectId, Range.clazz(classId), target, score);
        rankMaps.put("rankClassIndex", rankClassIndex);

        // 学生在学校排名
        int rankSchoolIndex = rankService.getRank(projectId, Range.school(schoolId), target, score);
        rankMaps.put("rankSchoolIndex", rankSchoolIndex);

        // 班级平均分/得分率
        double classAvg = averageService.getAverage(projectId, Range.clazz(classId), target);
        rankMaps.put("classAvg", DoubleUtils.round(classAvg));
        rankMaps.put("classAvgRate", DoubleUtils.round(fullScore == 0 ? 0 : classAvg / fullScore, true));

        // 学校平均分/得分率
        double schoolAvg = averageService.getAverage(projectId, Range.school(schoolId), target);
        rankMaps.put("schoolAvg", DoubleUtils.round(schoolAvg));
        rankMaps.put("schoolAvgRate", DoubleUtils.round(fullScore == 0 ? 0 : schoolAvg / fullScore, true));

        return rankMaps;
    }
}
