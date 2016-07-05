package com.xz.api.server.project;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.api.annotation.Function;
import com.xz.api.annotation.Parameter;
import com.xz.api.annotation.Type;
import com.xz.api.server.Server;
import com.xz.bean.Point;
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
import java.util.stream.Collectors;

/**
 * 总体成绩-知识点能力层级(双向细目)分析
 *
 * @author zhaorenwu
 */
@Function(description = "总体成绩-知识点能力层级(双向细目)分析", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "subjectId", type = Type.String, description = "科目ID", required = true),
        @Parameter(name = "schoolId", type = Type.String, description = "学校id", required = true)
})
@Service
public class ProjectPointAbilityLevelAnalysis implements Server {

    @Autowired
    PointService pointService;

    @Autowired
    QuestService questService;

    @Autowired
    FullScoreService fullScoreService;

    @Autowired
    AverageService averageService;

    @Autowired
    AbilityLevelService abilityLevelService;

    @Autowired
    ProjectService projectService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String subjectId = param.getString("subjectId");
        String schoolId = param.getString("schoolId");

        Range range = Range.school(schoolId);
        PointService.AbilityLevel[] abilityLevels = PointService.AbilityLevel.values();
        String studyStage = projectService.findProjectStudyStage(projectId);
        Map<String, Document> levelMap = abilityLevelService.queryAbilityLevels(studyStage, subjectId);

        List<Map<String, Object>> pointStats = getPointAnalysis(projectId, subjectId, range, abilityLevels, levelMap,
                pointService, questService, fullScoreService, averageService);
        Map<String, Object> abilityLevelStat = getAbilityLevelStats(projectId, subjectId, range,
                abilityLevels, levelMap, fullScoreService, averageService);

        return Result.success()
                .set("points", pointStats)
                .set("levels", abilityLevelStat);
    }

    // 获取知识点统计分析
    public static List<Map<String, Object>> getPointAnalysis(String projectId, String subjectId, Range range,
                                                             PointService.AbilityLevel[] abilityLevels,
                                                             Map<String, Document> levelMap,
                                                             PointService pointService,
                                                             QuestService questService,
                                                             FullScoreService fullScoreService,
                                                             AverageService averageService) {
        List<Map<String, Object>> pointStats = new ArrayList<>();
        List<Point> points = pointService.getPoints(projectId, subjectId);
        for (Point point : points) {
            String pointId = point.getId();
            Map<String, Object> pointStat = new HashMap<>();
            pointStat.put("pointName", point.getName());
            pointStat.put("pointId", pointId);

            // 知识点满分
            pointStat.put("fullScore", fullScoreService.getFullScore(projectId, Target.point(pointId)));

            // 知识点平均得分
            double score = averageService.getAverage(projectId, range, Target.point(pointId));
            pointStat.put("score", DoubleUtils.round(score));

            // 知识点-能力层级得分
            List<Map<String, Object>> pointLevels = new ArrayList<>();
            for (PointService.AbilityLevel level : abilityLevels) {
                Map<String, Object> pointLevel = new HashMap<>();

                String levelId = level.name();
                Document levelInfo = levelMap.get(levelId);
                List<Document> quests = questService.getQuests(projectId, pointId, levelId);
                List<String> questNos = quests.stream().map(document ->
                        document.getString("questNo")).collect(Collectors.toList());

                pointLevel.put("questNos", questNos);
                pointLevel.put("levelId", levelId);
                pointLevel.put("levelName", levelInfo == null ? ("能力层级" + levelId) : levelInfo.getString("level_name"));

                if (!questNos.isEmpty()) {
                    double fullScore = getFullScore(projectId, quests, fullScoreService);
                    pointLevel.put("fullScore", fullScore);

                    double avgScore = averageService.getAverage(projectId, range, Target.pointLevel(pointId, levelId));
                    pointLevel.put("avgScore", DoubleUtils.round(avgScore));

                    pointLevel.put("scoreRate", DoubleUtils.round(fullScore == 0 ? 0 : avgScore / fullScore, true));
                }

                pointLevels.add(pointLevel);
            }

            pointStat.put("pointLevels", pointLevels);
            pointStats.add(pointStat);
        }

        return pointStats;
    }

    // 获取题目集合总分
    private static double getFullScore(String projectId, List<Document> quests, FullScoreService fullScoreService) {
        double fullScore = 0;
        for (Document quest : quests) {
            String questId = quest.getString("questId");
            fullScore += fullScoreService.getFullScore(projectId, Target.quest(questId));
        }

        return fullScore;
    }

    // 获取能力层级统计分析
    public static Map<String, Object> getAbilityLevelStats(String projectId, String subjectId, Range range,
                                                           PointService.AbilityLevel[] abilityLevels,
                                                           Map<String, Document> levelMap,
                                                           FullScoreService fullScoreService,
                                                           AverageService averageService) {

        double totalScore = 0, userScore = 0;
        Map<String, Object> levelStat = new HashMap<>();
        List<Map<String, Object>> levelInfos = new ArrayList<>();
        for (PointService.AbilityLevel level : abilityLevels) {
            String levelId = level.name();
            Document levelInfo = levelMap.get(levelId);
            Map<String, Object> levelStatMap = new HashMap<>();
            levelStatMap.put("name", levelInfo == null ? ("能力层级" + levelId) : levelInfo.getString("level_name"));
            levelStatMap.put("levelId", levelId);

            // 能力层级满分
            Target target = Target.subjectLevel(subjectId, levelId);
            double fullScore = fullScoreService.getFullScore(projectId, target);
            levelStatMap.put("fullScore", fullScore);

            // 能力层级得分
            double avgScore = averageService.getAverage(projectId, range, target);
            levelStatMap.put("avgScore", DoubleUtils.round(avgScore));
            levelStatMap.put("scoreRate", DoubleUtils.round(fullScore == 0 ? 0 : avgScore / fullScore, true));

            totalScore += fullScore;
            userScore += avgScore;
            levelInfos.add(levelStatMap);
        }

        levelStat.put("levelInfos", levelInfos);
        levelStat.put("subjectId", subjectId);
        levelStat.put("totalScore", DoubleUtils.round(totalScore));
        levelStat.put("userScore", DoubleUtils.round(userScore));

        return levelStat;
    }
}
