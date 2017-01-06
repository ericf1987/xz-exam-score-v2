package com.xz.examscore.api.server.customization.examAlliance;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.report.Keys;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.*;
import com.xz.examscore.util.DoubleUtils;
import org.apache.commons.collections.MapUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author by fengye on 2017/1/4.
 */
@Function(description = "联考项目-题目能力层级分析", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true)
})
@Service
public class TotalQuestAbilityLevelAnalysis implements Server{

    @Autowired
    QuestAbilityLevelService questAbilityLevelService;

    @Autowired
    QuestAbilityLevelScoreService questAbilityLevelScoreService;

    @Autowired
    SubjectService subjectService;

    @Autowired
    StudentService studentService;

    @Autowired
    SchoolService schoolService;

    @Autowired
    AverageService averageService;

    @Autowired
    ScoreLevelService scoreLevelService;

    public static final String[] keys = new String[]{
            "level", "ability"
    };

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        List<String> subjectIds = subjectService.querySubjects(projectId);
        List<Document> projectSchools = schoolService.getProjectSchools(projectId);

        List<Map<String, Object>> result = new ArrayList<>();
        for(Document schoolDoc : projectSchools) {
            String schoolId = schoolDoc.getString("school");
            String schoolName = schoolService.getSchoolName(projectId, schoolId);
            Range schoolRange = Range.school(schoolId);
            int studentCount = studentService.getStudentCount(projectId, schoolRange, Target.project(projectId));
            List<Map<String, Object>> subjects = new ArrayList<>();
            for(String subjectId : subjectIds){
                Map<String, Object> subjectMap = getOneSubjectData(projectId, schoolRange, subjectId, keys);
                subjects.add(subjectMap);
            }
            Map<String, Object> schoolMap = new HashMap<>();
            schoolMap.put("schoolId", schoolId);
            schoolMap.put("schoolName", schoolName);
            schoolMap.put("studentCount", studentCount);
            schoolMap.put("subjects", subjects);
            schoolMap.put("total", addTotalMap(projectId, schoolRange, subjects, subjectIds));
            result.add(schoolMap);
        }

        return Result.success().set("totalQuestAbilityLevel", result);
    }

    private Map<String, Object> getOneSubjectData(String projectId, Range schoolRange, String subjectId, String[] keys) {
        Map<String, Object> subjectMap = new HashMap<>();
        subjectMap.put("subjectId", subjectId);
        subjectMap.put("subjectName", SubjectService.getSubjectName(subjectId));
        for (String levelOrAbility : keys){
            String questAbilityLevel = questAbilityLevelService.getId(subjectId, levelOrAbility);
            double totalScore = questAbilityLevelScoreService.getTotalScore(projectId, questAbilityLevel, subjectId, levelOrAbility, schoolRange);
            int count = questAbilityLevelScoreService.getStudentCount(projectId, questAbilityLevel, subjectId, levelOrAbility, schoolRange);
            double average = count == 0 ? 0 : DoubleUtils.round(totalScore / count);
            double factor = levelOrAbility.equals("level") ? 0.6d : (levelOrAbility.equals("ability") ? 0.7d : 0.85d);
            int c = questAbilityLevelScoreService.filterStudentList(projectId, questAbilityLevel, subjectId, levelOrAbility, schoolRange, factor).size();
            double rate = count == 0 ? 0 : DoubleUtils.round((double) c / count, true) ;
            Map<String, Object> levelOrAbilityMap = new HashMap<>();
            levelOrAbilityMap.put("average", DoubleUtils.round(average));
            levelOrAbilityMap.put("rate", DoubleUtils.round(rate, true));
            levelOrAbilityMap.put("count", c);
            levelOrAbilityMap.put("totalCount", count);
            subjectMap.put(levelOrAbility, levelOrAbilityMap);
        }
        double average = averageService.getAverage(projectId, schoolRange, Target.subject(subjectId));
        List<Document> scoreLevelRate = scoreLevelService.getScoreLevelRate(projectId, schoolRange, Target.subject(subjectId));
        double rate = scoreLevelRate.stream().filter(s -> s.getString("scoreLevel").equals(Keys.ScoreLevel.Excellent.name()))
                .mapToDouble(s -> MapUtils.getDouble(s, "rate")).sum();
        double c = scoreLevelRate.stream().filter(s -> s.getString("scoreLevel").equals(Keys.ScoreLevel.Excellent.name()))
                .mapToDouble(s -> MapUtils.getDouble(s, "count")).sum();
        Map<String, Object> totalMap = new HashMap<>();
        totalMap.put("average", DoubleUtils.round(average));
        totalMap.put("rate", DoubleUtils.round(rate, true));
        totalMap.put("count", c);
        subjectMap.put("total", totalMap);
        return subjectMap;
    }

    private Map<String, Object> addTotalMap(String projectId, Range schoolRange, List<Map<String, Object>> subjects, List<String> subjectIds) {
        Map<String, Object> totalMap = new HashMap<>();
        //累加水平检测总分
        double levelTotal = subjects.stream().mapToDouble(subject -> {
            Map<String, Object> level = (Map<String, Object>) subject.get("level");
            double average = MapUtils.getDouble(level, "average");
            return average;
        }).sum();

        //累加能力检测总分
        double abilityTotal = subjects.stream().mapToDouble(subject -> {
            Map<String, Object> level = (Map<String, Object>) subject.get("ability");
            double average = MapUtils.getDouble(level, "average");
            return average;
        }).sum();

        //能力检测良好率
        double averageGoodRate = getAverageGoodRate(subjects);

        //水平检测全科合格率
        double allPassRate = getAllPassRate(projectId, schoolRange, subjectIds, "level", 0.6d);

        List<Document> scoreLevelRate = scoreLevelService.getScoreLevelRate(projectId, schoolRange, Target.project(projectId));
        double excellentRate = scoreLevelRate.stream().filter(s -> s.getString("scoreLevel").equals(Keys.ScoreLevel.Excellent.name()))
                .mapToDouble(s -> MapUtils.getDouble(s, "rate")).sum();

        //总分
        Map<String, Object> t = new HashMap<>();
        t.put("score", DoubleUtils.round(levelTotal + abilityTotal));
        t.put("rate", DoubleUtils.round(excellentRate, true));

        //水平检测
        Map<String, Object> l = new HashMap<>();
        l.put("score", DoubleUtils.round(levelTotal));
        l.put("rate", DoubleUtils.round(allPassRate, true));

        //能力检测
        Map<String, Object> a = new HashMap<>();
        a.put("score", DoubleUtils.round(abilityTotal));
        a.put("rate", DoubleUtils.round(averageGoodRate, true));

        totalMap.put("total", t);
        totalMap.put("level", l);
        totalMap.put("ability", a);
        return totalMap;
    }

    private double getAllPassRate(String projectId, Range schoolRange, List<String> subjectIds, String level, double factor) {
        return questAbilityLevelScoreService.getAllPassCount(projectId, schoolRange, subjectIds, level, factor);
    }

    //计算全科良好率
    private double getAverageGoodRate(List<Map<String, Object>> subjects) {
        int count = 0;
        int total = 0;
        for (Map<String, Object> subject : subjects){
            Map<String, Object> ability = (Map<String, Object>)subject.get("ability");
            int c = MapUtils.getInteger(ability, "count");
            count += c;
            int t = MapUtils.getInteger(ability, "totalCount");
            total += t;
        }
        return total == 0 ? 0 : DoubleUtils.round((double) count / total);
    }
}
