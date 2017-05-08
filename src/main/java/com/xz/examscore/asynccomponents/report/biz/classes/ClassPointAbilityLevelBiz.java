package com.xz.examscore.asynccomponents.report.biz.classes;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.ajia.Param;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.bean.Point;
import com.xz.examscore.bean.PointLevel;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.*;
import com.xz.examscore.util.DoubleUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.xz.examscore.api.server.project.ProjectPointAbilityLevelAnalysis.filterLevels;

/**
 * @author by fengye on 2017/2/14.
 */
@Service
public class ClassPointAbilityLevelBiz implements Server {

    @Autowired
    TargetService targetService;

    @Autowired
    QuestService questService;

    @Autowired
    ProjectService projectService;

    @Autowired
    AbilityLevelService abilityLevelService;

    @Autowired
    FullScoreService fullScoreService;

    @Autowired
    PointService pointService;

    @Autowired
    AverageService averageService;

    @Override
    public Result execute(Param param) throws Exception {

        String projectId = param.getString("projectId");
        String subjectId = param.getString("subjectId");
        String classId = param.getString("classId");

        //当前班级的所有知识点能力层级得分
        ArrayList<Document> pointLevelScores = averageService.getAverageByRangeAndTargetName(projectId, Range.clazz(classId), Target.POINT_LEVEL);

        ArrayList<Document> pointScores = averageService.getAverageByRangeAndTargetName(projectId, Range.clazz(classId), Target.POINT);

        //筛选出当前科目的知识点能力层级
        List<Document> pointLevelSubject = getPointLevelSubject(projectId, subjectId, pointLevelScores);

        //获取当前科目的试题
        List<Document> quests = questService.getQuests(projectId, subjectId);

        String studyStage = projectService.findProjectStudyStage(projectId);
        Map<String, Document> levelMap = abilityLevelService.queryAbilityLevels(studyStage, subjectId);

        levelMap = filterLevels(projectId, subjectId, levelMap, fullScoreService);

        List<Map<String, Object>> pointStats = packPointStats(projectId, subjectId, pointLevelSubject, pointScores, quests, levelMap);
        Map<String, Object> abilityLevelStat = packAbilityLevelStats(subjectId, levelMap);

        return Result.success()
                .set("points", pointStats)
                .set("levels", abilityLevelStat);
    }

    protected List<Document> getPointLevelSubject(String projectId, String subjectId, ArrayList<Document> pointLevelScores) {
        return pointLevelScores.stream().filter(p -> {
            Document targetDoc = (Document) p.get("target");
            Document pointLevelId = (Document) targetDoc.get("id");
            PointLevel pointLevel = new PointLevel(pointLevelId.getString("point"), pointLevelId.getString("level"));
            return subjectId.equals(targetService.getTargetSubjectId(projectId, Target.pointLevel(pointLevel)));
        }).collect(Collectors.toList());
    }

    protected List<Map<String, Object>> packPointStats(String projectId, String subjectId, List<Document> pointLevelSubject, List<Document> pointScores, List<Document> quests, Map<String, Document> levelMap) {
        List<Point> points = pointService.getPoints(projectId, subjectId);
        List<Map<String, Object>> pointStats = new ArrayList<>();

        for (Point point : points) {
            String pointId = point.getId();
            double score = calScoreInPoint(pointId, pointScores);
            Map<String, Object> pointStat = new HashMap<>();

            pointStat.put("pointName", point.getName());
            pointStat.put("pointId", pointId);
            pointStat.put("fullScore", fullScoreService.getFullScore(projectId, Target.point(pointId)));
            pointStat.put("score", DoubleUtils.round(score));

            List<Map<String, Object>> pointLevels = new ArrayList<>();
            for (String levelId : levelMap.keySet()) {
                Map<String, Object> pointLevel = new HashMap<>();
                if (pointId.equals("1023816") && levelId.equals("C")) {
                    System.out.println("123");
                }
                Document levelInfo = levelMap.get(levelId);
                List<Document> targetQuests = findQuests(pointId, levelId, quests);
                List<String> questNos = targetQuests.stream().map(document ->
                        document.getString("questNo")).collect(Collectors.toList());

                pointLevel.put("questNos", questNos);
                pointLevel.put("levelId", levelId);
                pointLevel.put("levelName", levelInfo.getString("level_name"));

                if (!questNos.isEmpty()) {
                    double fullScore = getFullScore(projectId, targetQuests);
                    pointLevel.put("fullScore", fullScore);

                    double avgScore = calScoreInPointLevel(pointId, levelId, pointLevelSubject);
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

    double getFullScore(String projectId, List<Document> quests) {
        double fullScore = 0;
        for (Document quest : quests) {
            String questId = quest.getString("questId");
            fullScore += fullScoreService.getFullScore(projectId, Target.quest(questId));
        }
        return fullScore;
    }

    //根据知识点和能力层级获取包含知识点能力层级的试题列表
    public List<Document> findQuests(String pointId, String levelId, List<Document> quests) {
        List<Document> result = new ArrayList<>();
        for (Document quest : quests) {
            Document points = (Document) quest.get("points");

            if (null != points && !points.isEmpty()) {
                if (points.containsKey(pointId)) {
                    List<String> levels = (List<String>) points.get(pointId);
                    if (levels.contains(levelId)) {
                        result.add(quest);
                    }
                }
            }

        }
        return result;
    }

    protected Map<String, Object> packAbilityLevelStats(String subjectId, Map<String, Document> levelMap) {
        Map<String, Object> abilityLevelMap = new HashMap<>();
        abilityLevelMap.put("subjectId", subjectId);
        List<Map<String, Object>> levelInfos = new ArrayList<>();
        for (String levelId : levelMap.keySet()) {
            Map<String, Object> map = new HashMap<>();
            Document levelInfo = levelMap.get(levelId);
            map.put("levelId", levelId);
            map.put("name", levelInfo.getString("level_name"));
            levelInfos.add(map);
        }
        abilityLevelMap.put("levelInfos", levelInfos);
        return abilityLevelMap;
    }

    //计算过滤好的集合中指定知识点平均分
    double calScoreInPoint(String pointId, List<Document> pointScores) {
        for (Document doc : pointScores) {
            Document targetDoc = (Document) doc.get("target");
            String pid = targetDoc.getString("id");
            if (pointId.equals(pid)) {
                return doc.getDouble("average");
            }
        }
        return 0;
    }

    //计算过滤好的集合中指定知识点能力层级平均分
    double calScoreInPointLevel(String pointId, String levelId, List<Document> pointLevelSubject) {
        for (Document doc : pointLevelSubject) {
            Document targetDoc = (Document) doc.get("target");
            Document id = (Document) targetDoc.get("id");
            String pid = id.getString("point");
            String lid = id.getString("level");
            if (pointId.equals(pid) && levelId.equals(lid)) {
                return doc.getDouble("average");
            }
        }
        return 0;
    }

}
