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
            schoolMap.put("total", addTotalMap(subjects));
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
            double factor = levelOrAbility.equals("level") ? 0.6d : 0.7d;
            int c = questAbilityLevelScoreService.filterStudentList(projectId, questAbilityLevel, subjectId, levelOrAbility, schoolRange, factor).size();
            double rate = count == 0 ? 0 : DoubleUtils.round((double) c / count, true) ;
            Map<String, Object> levelOrAbilityMap = new HashMap<>();
            levelOrAbilityMap.put("average", average);
            levelOrAbilityMap.put("rate", rate);
            subjectMap.put(levelOrAbility, levelOrAbilityMap);
        }
        double average = averageService.getAverage(projectId, schoolRange, Target.subject(subjectId));
        List<Document> scoreLevelRate = scoreLevelService.getScoreLevelRate(projectId, schoolRange, Target.subject(subjectId));
        double rate = scoreLevelRate.stream().filter(s -> s.getString("scoreLevel").equals(Keys.ScoreLevel.Excellent.name()))
                .mapToDouble(s -> MapUtils.getDouble(s, "rate")).sum();
        Map<String, Object> totalMap = new HashMap<>();
        totalMap.put("average", average);
        totalMap.put("rate", rate);
        subjectMap.put("total", totalMap);
        return subjectMap;
    }

    private Map<String, Object> addTotalMap(List<Map<String, Object>> subjects) {
        Map<String, Object> totalMap = new HashMap<>();
        double levelTotal = subjects.stream().mapToDouble(subject -> {
            Map<String, Object> level = (Map<String, Object>) subject.get("level");
            double average = MapUtils.getDouble(level, "average");
            return average;
        }).sum();

        double abilityTotal = subjects.stream().mapToDouble(subject -> {
            Map<String, Object> level = (Map<String, Object>) subject.get("ability");
            double average = MapUtils.getDouble(level, "average");
            return average;
        }).sum();

        totalMap.put("levelTotal", levelTotal);
        totalMap.put("abilityTotal", abilityTotal);
        totalMap.put("totalScore", levelTotal + abilityTotal);
        totalMap.put("subjectId", "");
        totalMap.put("subjectName", "总分");
        return totalMap;
    }

}
