package com.xz.api.server.classes;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.lang.StringUtil;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xz.api.server.classes.ClassPointAnalysis.initSubject;
import static com.xz.api.server.project.ProjectPointAbilityLevelAnalysis.filterLevels;

/**
 * 班级成绩-能力层级分析
 *
 * @author zhaorenwu
 */

@Function(description = "班级成绩-能力层级分析", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "subjectId", type = Type.String, description = "科目id,默认第一个科目", required = false),
        @Parameter(name = "classId", type = Type.String, description = "班级id", required = true)
})
@Service
public class ClassAbilityLevelAnalysis implements Server {

    @Autowired
    SubjectService subjectService;

    @Autowired
    ScoreService scoreService;

    @Autowired
    FullScoreService fullScoreService;

    @Autowired
    StudentService studentService;

    @Autowired
    AverageService averageService;

    @Autowired
    ProjectService projectService;

    @Autowired
    AbilityLevelService abilityLevelService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String subjectId = param.getString("subjectId");
        String classId = param.getString("classId");

        // 初始化科目id
        if (StringUtil.isBlank(subjectId)) {
            subjectId = initSubject(projectId, subjectService);
        }

        if (StringUtil.isBlank(subjectId)) {
            return Result.fail("找不到考试科目信息");
        }

        String studyStage = projectService.findProjectStudyStage(projectId);
        Map<String, Document> levelMap = abilityLevelService.queryAbilityLevels(studyStage, subjectId);
        levelMap = filterLevels(projectId, subjectId, levelMap, fullScoreService);

        List<Map<String, Object>> classLevelAnalysis = getClassAbilityLevelAnalysis(
                projectId, subjectId, classId, levelMap);
        List<Map<String, Object>> studentLevelAnalysis = getStudentAbilityLevelAnalysis(
                projectId, subjectId, classId, levelMap);
        return Result.success()
                .set("classes", classLevelAnalysis)
                .set("students", studentLevelAnalysis)
                .set("hasHeader", !classLevelAnalysis.isEmpty());
    }

    // 学生能力层级分析
    private List<Map<String, Object>> getStudentAbilityLevelAnalysis(String projectId, String subjectId,
                                                                     String classId, Map<String, Document> levelMap) {
        List<Map<String, Object>> list = new ArrayList<>();

        List<Document> studentList = studentService.getStudentList(projectId, Range.clazz(classId));
        for (Document student : studentList) {
            Map<String, Object> map = new HashMap<>();

            String studentId = student.getString("student");
            String studentName = student.getString("name");
            map.put("studentId", studentId);
            map.put("studentName", studentName);

            Range range = Range.student(studentId);
            map.put("subjectScore", scoreService.getScore(projectId, range, Target.subject(subjectId)));
            map.put("levelStats", getLevelStats(projectId, subjectId, range, levelMap));
            list.add(map);
        }

        list.sort((o1, o2) -> ((Double) o2.get("subjectScore")).compareTo(((Double) o1.get("subjectScore"))));
        return list;
    }

    // 班级能力层级分析
    private List<Map<String, Object>> getClassAbilityLevelAnalysis(String projectId, String subjectId,
                                                                   String classId, Map<String, Document> levelMap) {
        Range range = Range.clazz(classId);
        return getLevelStats(projectId, subjectId, range, levelMap);
    }

    private List<Map<String, Object>> getLevelStats(String projectId, String subjectId,
                                                    Range range, Map<String, Document> levelMap) {
        List<Map<String, Object>> levelStats = new ArrayList<>();
        for (String levelId : levelMap.keySet()) {
            Document levelInfo = levelMap.get(levelId);

            Map<String, Object> levelStat = new HashMap<>();
            levelStat.put("levelName", levelInfo.getString("level_name"));
            levelStat.put("levelId", levelId);

            Target target = Target.subjectLevel(subjectId, levelId);
            double score;

            if (range.match(Range.STUDENT)) {
                score = scoreService.getScore(projectId, range, target);
            } else {
                score = averageService.getAverage(projectId, range, target);
            }
            levelStat.put("score", DoubleUtils.round(score));

            double fullScore = fullScoreService.getFullScore(projectId, target);
            levelStat.put("fullScore", fullScore);
            levelStat.put("scoreRate", DoubleUtils.round(score / fullScore, true));

            levelStats.add(levelStat);
        }

        return levelStats;
    }
}
