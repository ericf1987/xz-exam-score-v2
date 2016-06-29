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

        List<Map<String, Object>> classLevelAnalysis = getClassAbilityLevelAnalysis(projectId, subjectId, classId);
        List<Map<String, Object>> studentLevelAnalysis = getStudentAbilityLevelAnalysis(projectId, subjectId, classId);
        return Result.success().set("classes", classLevelAnalysis).set("students", studentLevelAnalysis);
    }

    // 学生能力层级分析
    private List<Map<String, Object>> getStudentAbilityLevelAnalysis(String projectId, String subjectId, String classId) {
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
            map.put("levelStats", getLevelStats(projectId, subjectId, range));
            list.add(map);
        }

        list.sort((o1, o2) -> ((Double) o2.get("subjectScore")).compareTo(((Double) o1.get("subjectScore"))));
        return list;
    }

    // 班级能力层级分析
    private List<Map<String, Object>> getClassAbilityLevelAnalysis(String projectId, String subjectId, String classId) {
        Range range = Range.clazz(classId);
        return getLevelStats(projectId, subjectId, range);
    }

    private List<Map<String, Object>> getLevelStats(String projectId, String subjectId, Range range) {
        List<Map<String, Object>> levelStats = new ArrayList<>();
        PointService.AbilityLevel[] abilityLevels = PointService.AbilityLevel.values();
        for (PointService.AbilityLevel level : abilityLevels) {
            String levelName = level.name();
            Map<String, Object> levelStat = new HashMap<>();
            levelStat.put("levelName", levelName);

            Target target = Target.subjectLevel(subjectId, levelName);
            double score;

            if (range.match(Range.STUDENT)) {
                score = scoreService.getScore(projectId, range, target);
            } else {
                score = averageService.getAverage(projectId, range, target);
            }
            levelStat.put("score", DoubleUtils.round(score));

            double fullScore = fullScoreService.getFullScore(projectId, target);
            levelStat.put("fullScore", fullScore);
            levelStat.put("scoreRate", DoubleUtils.round(fullScore == 0 ? 0 : score / fullScore, true));

            levelStats.add(levelStat);
        }

        return levelStats;
    }

    public static String initSubject(String projectId, SubjectService subjectService) {
        List<String> subjectIds = subjectService.querySubjects(projectId);
        subjectIds.sort(String::compareTo);

        if (!subjectIds.isEmpty()) {
            return subjectIds.get(0);
        }

        return null;
    }
}
