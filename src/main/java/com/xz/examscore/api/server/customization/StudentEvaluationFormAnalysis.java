package com.xz.examscore.api.server.customization;

import com.hyd.appserver.utils.StringUtils;
import com.mongodb.client.FindIterable;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
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

import java.text.Collator;
import java.util.*;
import java.util.stream.Collectors;

import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;
import static com.xz.ajiaedu.common.mongo.MongoUtils.toList;

/**
 * @author by fengye on 2016/12/8.
 */
@Function(description = "学生测评报告分析", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "schoolId", type = Type.String, description = "学校ID", required = false),
        @Parameter(name = "classId", type = Type.String, description = "班级ID", required = false),
        @Parameter(name = "pageSize", type = Type.String, description = "每页查询学生数量", required = true),
        @Parameter(name = "pageCount", type = Type.String, description = "页码数", required = true)
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

    @Autowired
    StudentEvaluationByRankAnalysis studentEvaluationByRankAnalysis;

    @Override
    public Result execute(Param param) throws Exception {
        String schoolId = param.getString("schoolId");
        String classId = param.getString("classId");
        if(!StringUtil.isBlank(schoolId) && !StringUtils.isBlank(classId)){
            return mainProcess(param);
        }
        return studentEvaluationByRankAnalysis.execute(param);
    }

    public Result mainProcess(Param param) {
        String projectId = param.getString("projectId");
        String schoolId = param.getString("schoolId");
        String classId = param.getString("classId");
        int pageSize = Integer.valueOf(param.getString("pageSize"));
        int pageCount = Integer.valueOf(param.getString("pageCount"));
        Document projectDoc = projectService.findProject(projectId);
        String category = projectDoc.getString("category");
        Range provinceRange = Range.province(provinceService.getProjectProvince(projectId));
        List<String> subjectIds = subjectService.querySubjects(projectId);
        //存放文理单科
        List<String> wlSubjectIds = subjectIds.stream().filter(wl -> SubjectCombinationService.isW(wl) || SubjectCombinationService.isL(wl)).collect(Collectors.toList());
        //综合科目
        List<String> combinedSubjectIds = subjectCombinationService.getAllSubjectCombinations(projectId);
        //全科参考学生
        int studentCount = studentService.getStudentCount(projectId, Range.province(provinceService.getProjectProvince(projectId)), Target.project(projectId));
        List<Map<String, Object>> resultList = new ArrayList<>();

        //本科上线预测
        List<Double> entryLevelScoreLine = projectConfigService.getEntryLevelScoreLine(projectId,
                Range.province(provinceService.getProjectProvince(projectId)), Target.project(projectId), studentCount);

        FindIterable<Document> projectStudentList = studentService.getProjectStudentList(projectId, Range.clazz(classId),
                pageSize, pageSize * pageCount, doc("student", 1).append("name", 1));

        List<Document> projectStudents = toList(projectStudentList);
        Collections.sort(projectStudents, (Document d1, Document d2) ->{
            String name1 = d1.getString("name");
            String name2 = d2.getString("name");
            Collator collator= Collator.getInstance(java.util.Locale.CHINA);
            return collator.compare(name1, name2);
        });

        //项目最高最低分
        double[] minMaxScore = minMaxScoreService.getMinMaxScore(projectId, Range.province(provinceService.getProjectProvince(projectId)), Target.project(projectId));
        double min = minMaxScore[0];
        double max = minMaxScore[1];

        LinkedList<Double> scoreLine = new LinkedList<>(entryLevelScoreLine);
        scoreLine.addFirst(max);
        scoreLine.addLast(min);

        List<List<Map<String, Object>>> entryLevelList = new ArrayList<>();
        queryRangeAverageInEntryLevel(projectId, provinceRange, subjectIds, combinedSubjectIds, entryLevelList);

        for (Document studentDoc : projectStudents) {
            Map<String, Object> studentMap = new HashMap<>();
            //统计基础信息
            String studentId = studentDoc.getString("student");
            if(!isRequiredStudent(projectId, studentId, subjectIds)){
                continue;
            }
            Map<String, String> studentBaseInfo = new HashMap<>();
            studentBaseInfo.put("studentId", studentId);
            studentBaseInfo.put("studentName", studentDoc.getString("name"));
            studentBaseInfo.put("className", classService.getClassName(projectId, classId));
            studentBaseInfo.put("schoolName", schoolService.getSchoolName(projectId, schoolId));
            studentBaseInfo.put("category", category);
            studentMap.put("studentBaseInfo", studentBaseInfo);

            //查询得分及排名
            Map<String, Object> scoreAndRankMap = new HashMap<>();
            scoreAndRankMap.put("project", getScoreAndRankMap(projectId, schoolId, classId, studentId, Target.project(projectId)));
            //语数外得分及排名
            List<Map<String, Object>> subjectScoreAndRank = new ArrayList<>();
            subjectIds.stream().filter(s -> !SubjectCombinationService.isW(s) && !SubjectCombinationService.isL(s)).forEach(subjectId -> {
                Map<String, Object> map = getSingleSubjectRankAndLevel(projectId, schoolId, classId, studentId, Target.subject(subjectId), getQuestTypeScoreMap(projectId, studentId, subjectId), getPointScoreMap(projectId, studentId, subjectId), getSubjectAbilityLevel(projectId, studentId, subjectId));
                subjectScoreAndRank.add(map);
            });
            scoreAndRankMap.put("subjects", subjectScoreAndRank);

            //文理单科得分及排名
            List<Map<String, Object>> wlSubjectScoreAndRank = new ArrayList<>();
            wlSubjectIds.forEach(wlSubjectId -> {
                Map<String, Object> map = getSingleSubjectRankAndLevel(projectId, schoolId, classId, studentId, Target.subject(wlSubjectId), getQuestTypeScoreMap(projectId, studentId, wlSubjectId), getPointScoreMap(projectId, studentId, wlSubjectId), getSubjectAbilityLevel(projectId, studentId, wlSubjectId));
                wlSubjectScoreAndRank.add(map);
            });
            scoreAndRankMap.put("wlSubjects", wlSubjectScoreAndRank);

            //查询组合科目得分及排名
            List<Map<String, Object>> combinedSubjectScoreAndRank = new ArrayList<>();
            combinedSubjectIds.forEach(combinedSubjectId -> {
                Map<String, Object> map = getScoreAndRankMap(projectId, schoolId, classId, studentId, Target.subjectCombination(combinedSubjectId));
                combinedSubjectScoreAndRank.add(map);
            });

            scoreAndRankMap.put("combinedSubjects", combinedSubjectScoreAndRank);

            studentMap.put("scoreAndRankMap", scoreAndRankMap);

            resultList.add(studentMap);

            //resultList = filterScoreZero(resultList);
        }
        return Result.success().set("scoreLine", scoreLine).set("entryLevelList", entryLevelList).set("studentList", resultList);
    }

    public void queryRangeAverageInEntryLevel(String projectId, Range provinceRange, List<String> subjectIds, List<String> combinedSubjectIds, List<List<Map<String, Object>>> entryLevelList) {
        //查询各个本科录取段内，各个目标维度的平均分
        List<Document> entryLevelDoc = collegeEntryLevelService.getEntryLevelDoc(projectId);
        Collections.sort(entryLevelDoc, (Document d1, Document d2) -> {
            Double s1 = d1.getDouble("score");
            Double s2 = d2.getDouble("score");
            return s2.compareTo(s1);
        });
        List<String> entryLevelKey = entryLevelDoc.stream().map(doc -> doc.getString("level")).collect(Collectors.toList());
        //增加统计本科未上线学生的平均得分
        entryLevelKey.add("OFFLINE");
        for (String key : entryLevelKey) {
            //科目
            List<Map<String, Object>> averagesInLevel = new ArrayList<>();
            for (String subjectId : subjectIds.stream().filter(s -> !SubjectCombinationService.isW(s) && !SubjectCombinationService.isL(s)).collect(Collectors.toList())) {
                Map<String, Object> averageInLevel = getAveragesInLevel(projectId, provinceRange, key, subjectId);
                averagesInLevel.add(averageInLevel);
            }
            //组合科目
            for (String combinedSubjectId : combinedSubjectIds) {
                Map<String, Object> averageInLevel = new HashMap<>();
                double average = collegeEntryLevelAverageService.getAverage(projectId, provinceRange, Target.subjectCombination(combinedSubjectId), key);
                String combinedSubjectName = SubjectService.getSubjectName(combinedSubjectId);
                averageInLevel.put("subjectId", combinedSubjectId);
                averageInLevel.put("subjectName", combinedSubjectName);
                averageInLevel.put("average", DoubleUtils.round(average));
                averagesInLevel.add(averageInLevel);
            }
            entryLevelList.add(averagesInLevel);
        }
    }

    public boolean isRequiredStudent(String projectId, String studentId, List<String> subjectIds) {
        //只有全科参考且总分不为0才满足条件
        boolean b = studentService.hasAllSubjects(projectId, studentId, subjectIds);
        double totalScore = scoreService.getScore(projectId, Range.student(studentId), Target.project(projectId));
        return b && totalScore != 0;
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

    public Map<String, Object> getAveragesInLevel(String projectId, Range provinceRange, String key, String subjectId) {
        Map<String, Object> averageInLevel = new HashMap<>();
        double average = collegeEntryLevelAverageService.getAverage(projectId, provinceRange, Target.subject(subjectId), key);
        String subjectName = SubjectService.getSubjectName(subjectId);
        averageInLevel.put("subjectId", subjectId);
        averageInLevel.put("subjectName", subjectName);
        averageInLevel.put("average", DoubleUtils.round(average));
        return averageInLevel;
    }

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

    public Map<String, Object> getPointScoreMap(String projectId, String studentId, String subjectId) {
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
