package com.xz.examscore.api.server.customization;

import com.mongodb.client.FindIterable;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.api.server.classes.ClassPointAnalysis;
import com.xz.examscore.api.server.project.ProjectPointAbilityLevelAnalysis;
import com.xz.examscore.api.server.project.ProjectQuestTypeAnalysis;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.*;
import com.xz.examscore.util.DoubleUtils;
import org.apache.commons.collections4.MapUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;

/**
 * @author by fengye on 2016/12/8.
 */
@Function(description = "学生测评报告分析", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "schoolId", type = Type.String, description = "学校ID", required = true),
        @Parameter(name = "projectId", type = Type.String, description = "班级ID", required = true),
        @Parameter(name = "pageSize", type = Type.String, description = "每页查询学生数量", required = true),
        @Parameter(name = "pageCount", type = Type.String, description = "页码数", required = true),
        @Parameter(name = "subjectCombinationId", type = Type.String, description = "组合科目ID", required = true)
})
@Service
public class StudentEvaluationFormAnalysis implements Server {

    @Autowired
    SchoolService schoolService;

    @Autowired
    ClassService classService;

    @Autowired
    StudentService studentService;

    @Autowired
    ProjectService projectService;

    @Autowired
    ScoreService scoreService;

    @Autowired
    RankService rankService;

    @Autowired
    SubjectService subjectService;

    @Autowired
    TargetService targetService;

    @Autowired
    MinMaxScoreService minMaxScoreService;

    @Autowired
    AverageService averageService;

    @Autowired
    ProjectConfigService projectConfigService;

    @Autowired
    ProvinceService provinceService;

    @Autowired
    QuestTypeService questTypeService;

    @Autowired
    FullScoreService fullScoreService;

    @Autowired
    QuestTypeScoreService questTypeScoreService;

    @Autowired
    ClassPointAnalysis classPointAnalysis;

    @Autowired
    PointService pointService;

    @Autowired
    AbilityLevelService abilityLevelService;

    @Autowired
    QuestService questService;

    @Autowired
    ProjectPointAbilityLevelAnalysis projectPointAbilityLevelAnalysis;

    @Autowired
    ProjectQuestTypeAnalysis projectQuestTypeAnalysis;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String schoolId = param.getString("schoolId");
        String classId = param.getString("classId");
        int pageSize = Integer.valueOf(param.getString("pageSize"));
        int pageCount = Integer.valueOf(param.getString("pageCount"));
        Document projectDoc = projectService.findProject(projectId);
        String category = projectDoc.getString("category");
        List<String> subjectIds = subjectService.querySubjects(projectId);
        int studentCount = studentService.getStudentCount(projectId, Range.province(provinceService.getProjectProvince(projectId)), Target.project(projectId));
        List<Map<String, Object>> resultList = new ArrayList<>();

        FindIterable<Document> projectStudentList = studentService.getProjectStudentList(projectId, Range.clazz(classId),
                pageSize, pageSize * pageCount, doc("student", 1).append("name", 1));

        for (Document studentDoc : projectStudentList) {
            Map<String, Object> studentMap = new HashMap<>();
            //统计基础信息
            String studentId = studentDoc.getString("student");
            Map<String, String> studentBaseInfo = new HashMap<>();
            studentBaseInfo.put("studentName", studentDoc.getString("name"));
            studentBaseInfo.put("className", classService.getClassName(projectId, classId));
            studentBaseInfo.put("schoolName", schoolService.getSchoolName(projectId, schoolId));
            studentBaseInfo.put("category", category);
            studentMap.put("studentBaseInfo", studentBaseInfo);

            //查询得分及排名
            Map<String, Object> scoreAndRankMap = new HashMap<>();
            scoreAndRankMap.put("project", getScoreAndRankMap(projectId, schoolId, classId, studentId, null));
            List<Map<String, Object>> subjectScoreAndRank = new ArrayList<>();
            subjectIds.forEach(subjectId -> {
                Map<String, Object> map = getScoreAndRankMap(projectId, schoolId, classId, studentId, subjectId);
                //统计各个科目的题型，知识点，双向细目情况
                map.put("questTypeScore", getQuestTypeScoreMap(projectId, studentId, subjectId));
                map.put("pointScore", getPointScoreMap(projectId, studentId, subjectId));
                map.put("pointAbilityLevel", getPointAbilityLevel(projectId, studentId, subjectId));
                subjectScoreAndRank.add(map);
            });
            scoreAndRankMap.put("subjects", subjectScoreAndRank);
            studentMap.put("scoreAndRankMap", scoreAndRankMap);

            //本科上线预测

            List<Double> entryLevelScoreLine = projectConfigService.getEntryLevelScoreLine(projectId,
                    Range.province(provinceService.getProjectProvince(projectId)), Target.project(projectId), studentCount);
            studentMap.put("entryLevelScoreLine", entryLevelScoreLine);
            resultList.add(studentMap);
        }
        return Result.success().set("studentList", resultList);
    }

    private List<Map<String, Object>> getPointAbilityLevel(String projectId, String studentId, String subjectId) {
        String studyStage = projectService.findProjectStudyStage(projectId);
        Map<String, Document> levelMap = abilityLevelService.queryAbilityLevels(studyStage, subjectId);
        return projectPointAbilityLevelAnalysis.getPointAnalysis(projectId, subjectId, Range.student(studentId), levelMap);
    }

    private Map<String, Object> getPointScoreMap(String projectId, String studentId, String subjectId) {
        Map<String, Object> pointScore = new HashMap<>();
        List<Map<String, Object>> pointStats = classPointAnalysis.getPointStats(projectId, subjectId, Range.student(studentId));
        Collections.sort(pointStats, (Map<String, Object> p1, Map<String, Object> p2) -> {
            Double r1 = MapUtils.getDouble(p1, "scoreRate");
            Double r2 = MapUtils.getDouble(p2, "scoreRate");
            return r2.compareTo(r1);
        });
        int size = pointStats.size();
        pointScore.put("top", pointStats.subList(0, size >= 5 ? 5 : size));
        pointScore.put("bottom", pointStats.subList(size >= 5 ? (size - 5) : 0, pointStats.size()));
        return pointScore;
    }

    private List<Map<String, Object>> getQuestTypeScoreMap(String projectId, String studentId, String subjectId) {
        return projectQuestTypeAnalysis.getQuestTypeAnalysis(projectId, subjectId, Range.student(studentId));
    }

    public Map<String, Object> getScoreAndRankMap(String projectId, String schoolId, String classId, String studentId, String subjectId) {
        Map<String, Object> scoreAndRank = new HashMap<>();
        Target target = targetService.getTarget(projectId, subjectId);
        double totalScore = scoreService.getScore(projectId, Range.student(studentId), target);
        int classRank = rankService.getRank(projectId, Range.clazz(classId), target, studentId);
        int schoolRank = rankService.getRank(projectId, Range.school(schoolId), target, studentId);
        scoreAndRank.put("totalScore", totalScore);
        scoreAndRank.put("classRank", classRank);
        scoreAndRank.put("schoolRank", schoolRank);
        scoreAndRank.put("subjectId", target.match(Target.PROJECT) ? "" : target.getId());

        //加入该科目的学校最高分，最低分，平均分
        double[] minMaxScore = minMaxScoreService.getMinMaxScore(projectId, Range.school(schoolId), target);
        double minScore = minMaxScore[0];
        double maxScore = minMaxScore[1];
        double avgScore = averageService.getAverage(projectId, Range.school(schoolId), target);
        scoreAndRank.put("schoolMinScore", minScore);
        scoreAndRank.put("schoolMaxScore", maxScore);
        scoreAndRank.put("schoolAvgScore", DoubleUtils.round(avgScore));
        return scoreAndRank;
    }
}
