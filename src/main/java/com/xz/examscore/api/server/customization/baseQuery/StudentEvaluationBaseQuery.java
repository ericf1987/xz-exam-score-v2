package com.xz.examscore.api.server.customization.baseQuery;

import com.xz.examscore.api.server.classes.ClassAbilityLevelAnalysis;
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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author by fengye on 2017/5/5.
 */
@Service
public class StudentEvaluationBaseQuery {


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
    ScoreRateService scoreRateService;

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

    @Autowired
    ClassAbilityLevelAnalysis classAbilityLevelAnalysis;

    @Autowired
    SubjectCombinationService subjectCombinationService;

    @Autowired
    CollegeEntryLevelService collegeEntryLevelService;

    @Autowired
    CollegeEntryLevelAverageService collegeEntryLevelAverageService;

    @Autowired
    FullScoreService fullScoreService;

    @Autowired
    StudentCompetitiveService studentCompetitiveService;

    public static final int POINT_COUNT = 5;

    /**
     * 获取各科本科线平均分
     *
     * @param projectId     项目ID
     * @param provinceRange 范围
     * @param key           本科参数
     * @param subjectId     科目
     * @return 返回结果
     */
    public Map<String, Object> getAveragesInLevel(String projectId, Range provinceRange, String key, String subjectId) {
        Map<String, Object> averageInLevel = new HashMap<>();
        double average = collegeEntryLevelAverageService.getAverage(projectId, provinceRange, Target.subject(subjectId), key);
        String subjectName = SubjectService.getSubjectName(subjectId);
        averageInLevel.put("subjectId", subjectId);
        averageInLevel.put("subjectName", subjectName);
        averageInLevel.put("average", DoubleUtils.round(average));
        return averageInLevel;
    }

    public Map<String, Object> getSingleSubjectRankAndLevel(String projectId, String schoolId, String classId, String studentId, Target subject, List<Map<String, Object>> questTypeScoreMap, Map<String, Object> pointScoreMap, List<Map<String, Object>> subjectAbilityLevel) {
        Map<String, Object> map = getScoreAndRankMap(projectId, schoolId, classId, studentId, subject);
        //统计各个科目的题型，知识点，双向细目情况
        map.put("questTypeScore", questTypeScoreMap);
        map.put("pointScore", pointScoreMap);
        //目前考虑将能力层级中得分为0的过滤
        //subjectAbilityLevel = subjectAbilityLevel.stream().filter(m -> MapUtils.getDouble(m, "score") != 0).collect(Collectors.toList());
        //根据得分率排序
        Collections.sort(subjectAbilityLevel, (Map<String, Object> m1, Map<String, Object> m2) -> {
            Double d1 = MapUtils.getDouble(m1, "scoreRate");
            Double d2 = MapUtils.getDouble(m2, "scoreRate");
            return d2.compareTo(d1);
        });
        map.put("subjectAbilityLevel", subjectAbilityLevel);
        return map;
    }

    /**
     * 获取科目能力层级数据
     *
     * @param projectId 项目ID
     * @param studentId 学生ID
     * @param subjectId 科目ID
     * @return 返回结果
     */
    public List<Map<String, Object>> getSubjectAbilityLevel(String projectId, String studentId, String subjectId) {
        String studyStage = projectService.findProjectStudyStage(projectId);
        Map<String, Document> levelMap = abilityLevelService.queryAbilityLevels(studyStage, subjectId);
        List<Map<String, Object>> classAbilityLevelList = classAbilityLevelAnalysis.getLevelStats(projectId, subjectId, Range.student(studentId), levelMap);
        //按得分率排序
        Collections.sort(classAbilityLevelList, (Map<String, Object> m1, Map<String, Object> m2) -> {
            Double r1 = MapUtils.getDouble(m1, "scoreRate");
            Double r2 = MapUtils.getDouble(m2, "scoreRate");
            return r2.compareTo(r1);
        });
        return classAbilityLevelAnalysis.getLevelStats(projectId, subjectId, Range.student(studentId), levelMap);
    }

    /**
     * 获取科目知识点数据
     *
     * @param projectId 项目ID
     * @param studentId 学生ID
     * @param subjectId 科目ID
     * @return 返回结果
     */
    public Map<String, Object> getPointScoreMap(String projectId, String studentId, String subjectId) {
        Map<String, Object> pointScore = new HashMap<>();
        List<Map<String, Object>> pointStats = classPointAnalysis.getPointStats(projectId, subjectId, Range.student(studentId));
        Collections.sort(pointStats, (Map<String, Object> p1, Map<String, Object> p2) -> {
            Double r1 = MapUtils.getDouble(p1, "scoreRate");
            Double r2 = MapUtils.getDouble(p2, "scoreRate");
            return r2.compareTo(r1);
        });
        int size = pointStats.size();
        pointScore.put("top", pointStats.subList(0, size >= POINT_COUNT ? POINT_COUNT : size));
        pointScore.put("bottom", pointStats.subList(size >= POINT_COUNT ? (size - POINT_COUNT) : 0, pointStats.size()));
        return pointScore;
    }

    /**
     * 获取科目题型数据
     *
     * @param projectId 项目ID
     * @param studentId 学生ID
     * @param subjectId 科目ID
     * @return 返回结果
     */
    public List<Map<String, Object>> getQuestTypeScoreMap(String projectId, String studentId, String subjectId) {
        List<Map<String, Object>> questTypeList = projectQuestTypeAnalysis.getQuestTypeAnalysis(projectId, subjectId, Range.student(studentId));
        //按得分率排序
        Collections.sort(questTypeList, (Map<String, Object> m1, Map<String, Object> m2) -> {
            Double s1 = MapUtils.getDouble(m1, "scoreRate");
            Double s2 = MapUtils.getDouble(m2, "scoreRate");
            return s2.compareTo(s1);
        });
        return questTypeList;
    }

    /**
     * 获取得分和排名数据
     *
     * @param projectId 项目ID
     * @param schoolId  学校ID
     * @param classId   班级ID
     * @param studentId 学生ID
     * @param target    目标
     * @return 返回结果
     */
    public Map<String, Object> getScoreAndRankMap(String projectId, String schoolId, String classId, String studentId, Target target) {
        Map<String, Object> scoreAndRank = new HashMap<>();
        double totalScore = scoreService.getScore(projectId, Range.student(studentId), target);
        int classRank = rankService.getRank(projectId, Range.clazz(classId), target, studentId);
        int schoolRank = rankService.getRank(projectId, Range.school(schoolId), target, studentId);
        int provinceRank = rankService.getRank(projectId, Range.province(provinceService.getProjectProvince(projectId)), target, studentId);
        double scoreRate = scoreRateService.getScoreRate(projectId, Range.student(studentId), target);
        double fullScore = fullScoreService.getFullScore(projectId, target);
        scoreAndRank.put("totalScore", totalScore);
        scoreAndRank.put("fullScore", fullScore);
        scoreAndRank.put("scoreRate", DoubleUtils.round(scoreRate));
        scoreAndRank.put("classRank", classRank);
        scoreAndRank.put("schoolRank", schoolRank);
        scoreAndRank.put("provinceRank", provinceRank);
        scoreAndRank.put("subjectId", target.match(Target.PROJECT) ? "" : target.getId());
        scoreAndRank.put("subjectName", target.match(Target.PROJECT) ? "全科" : SubjectService.getSubjectName(target.getId().toString()));

        //加入该科目的学校最高分，最低分，平均分
        double[] minMaxScore = minMaxScoreService.getMinMaxScore(projectId, Range.school(schoolId), target);
        double minScore = minMaxScore[0];
        double maxScore = minMaxScore[1];
        double avgScore = averageService.getAverage(projectId, Range.school(schoolId), target);
        scoreAndRank.put("schoolMinScore", minScore);
        scoreAndRank.put("schoolMaxScore", maxScore);
        scoreAndRank.put("schoolAvgScore", DoubleUtils.round(avgScore));

        //竞争对手平均分
        double competitiveAverage_province = studentCompetitiveService.getAverage(projectId, Range.province(provinceService.getProjectProvince(projectId)), target, provinceRank);
        double competitiveAverage_school = studentCompetitiveService.getAverage(projectId, Range.school(schoolId), target, schoolRank);
        scoreAndRank.put("competitiveAverage_province", DoubleUtils.round(competitiveAverage_province));
        scoreAndRank.put("competitiveAverage_school", DoubleUtils.round(competitiveAverage_school));
        return scoreAndRank;
    }
}
