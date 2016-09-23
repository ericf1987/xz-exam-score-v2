package com.xz.examscore.api.server.classes;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.*;
import com.xz.examscore.util.DoubleUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xz.examscore.api.server.project.ProjectTopStudentStat.filterSubject;

/**
 * 班级成绩-排名分析
 *
 * @author zhaorenwu
 */
@SuppressWarnings("unchecked")
@Function(description = "班级成绩-排名分析", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "schoolId", type = Type.String, description = "学校id", required = true),
        @Parameter(name = "classId", type = Type.String, description = "班级id", required = true),
        @Parameter(name = "authSubjectIds", type = Type.StringArray, description = "可访问科目范围，为空返回所有", required = false)
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
        String[] authSubjectIds = param.getStringValues("authSubjectIds");

        List<Map<String, Object>> rankstats = new ArrayList<>();
        List<Document> studentList = studentService.getStudentList(projectId, Range.clazz(classId));
        for (Document student : studentList) {
            Map<String, Object> map = new HashMap<>();

            //先取出该同学的全科班级排名
            double score = scoreService.getScore(projectId, Range.student(student.getString("student")), Target.project(projectId));
            int rankClassIndex = rankService.getRank(projectId, Range.clazz(classId), Target.project(projectId), score);

            String studentId = student.getString("student");
            String studentName = student.getString("name");

            map.put("examNo", student.getString("examNo"));
            map.put("studentId", studentId);
            map.put("studentName", studentName);

            // 项目排行分析
            Map<String, Object> projectRankMap = getRankAnalysisMap(
                    projectId, Target.project(projectId), schoolId, classId, studentId, rankClassIndex);
            map.put("projectRankStat", projectRankMap);

            // 科目排行分析
            List<Map<String, Object>> subjectRankList = getSubjectRankList(
                    projectId, schoolId, classId, studentId, authSubjectIds, rankClassIndex);
            map.put("subjectRankStat", subjectRankList);

            rankstats.add(map);
        }

        rankstats.sort((o1, o2) -> {
            Double score1 = (Double) ((Map<String, Object>) o1.get("projectRankStat")).get("score");
            Double score2 = (Double) ((Map<String, Object>) o2.get("projectRankStat")).get("score");
            return score2.compareTo(score1);
        });

        List<String> subjects = subjectService.querySubjects(projectId);
        return Result.success()
                .set("rankstats", rankstats)
                .set("hasHeader", !rankstats.isEmpty() && !subjects.isEmpty());
    }

    private List<Map<String, Object>> getSubjectRankList(
            String projectId, String schoolId, String classId, String studentId, String[] authSubjectIds, int rankClassIndex) {
        List<Map<String, Object>> subjectRankList = new ArrayList<>();

        // 复制查询结果，以免发生 ConcurrentModificationException 异常
        List<String> subjects = new ArrayList<>(subjectService.querySubjects(projectId));
        subjects = filterSubject(subjects, authSubjectIds);

        for (String subjectId : subjects) {
            Map<String, Object> rankAnalysisMap = getRankAnalysisMap(
                    projectId, Target.subject(subjectId), schoolId, classId, studentId, rankClassIndex);
            rankAnalysisMap.put("subjectId", subjectId);
            rankAnalysisMap.put("subjectName", SubjectService.getSubjectName(subjectId));
            subjectRankList.add(rankAnalysisMap);
        }

        return subjectRankList;
    }

    // 获取排行分析
    private Map<String, Object> getRankAnalysisMap(
            String projectId, Target target, String schoolId, String classId, String studentId, int rankClassIndex) {
        Map<String, Object> rankMaps = new HashMap<>();

        double fullScore = fullScoreService.getFullScore(projectId, target);

        // 总分
        rankMaps.put("fullScore", fullScore);

        // 学生得分/得分率
        double score = scoreService.getScore(projectId, Range.student(studentId), target);
        rankMaps.put("score", score);
        rankMaps.put("scoreRate", DoubleUtils.round(fullScore == 0 ? 0 : score / fullScore, true));

        // 学生在班级排名
        rankMaps.put("rankClassIndex", rankService.getRank(projectId, Range.clazz(classId), target, score));

        // 学生在学校排名
        int rankSchoolIndex = rankService.getRank(projectId, Range.school(schoolId), target, score);
        rankMaps.put("rankSchoolIndex", rankSchoolIndex);

        // 用学生在班级的全科排名作为参数，查询在学校范围内该排名的分数和得分率
        double scoreInSchool = rankService.getRankScore(projectId, Range.school(schoolId), target, rankClassIndex);
        rankMaps.put("rankInSchoolScore", scoreInSchool);
        rankMaps.put("rankInSchoolRate", DoubleUtils.round(fullScore == 0 ? 0 : scoreInSchool / fullScore, true));

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
