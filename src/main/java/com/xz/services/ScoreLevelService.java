package com.xz.services;

import com.mongodb.client.MongoDatabase;
import com.xz.ajiaedu.common.report.Keys;
import com.xz.bean.ProjectConfig;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.util.CollectionUtil;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;
import static com.xz.ajiaedu.common.report.Keys.ScoreLevel.*;
import static com.xz.util.Mongo.range2Doc;
import static com.xz.util.Mongo.target2Doc;

@Service
public class ScoreLevelService {

    @Autowired
    ProjectConfigService projectConfigService;

    @Autowired
    MongoDatabase scoreDatabase;

    /**
     * 根据得分率计算得分等级
     *
     * @param projectId 项目ID
     * @param scoreRate 得分率
     *
     * @return 得分等级
     */
    public String calculateScoreLevel(String projectId, double scoreRate) {
        ProjectConfig projectConfig = projectConfigService.getProjectConfig(projectId);
        Map<String, Double> scoreLevels = projectConfig.getScoreLevels();

        if (scoreRate >= scoreLevels.get(Excellent.name())) {
            return Excellent.name();
        } else if (scoreRate >= scoreLevels.get(Good.name())) {
            return Good.name();
        } else if (scoreRate >= scoreLevels.get(Pass.name())) {
            return Pass.name();
        } else {
            return Fail.name();
        }
    }

    /**
     * 查询得分等级
     *
     * @param projectId 项目ID
     * @param studentId 学生ID
     * @param target    目标
     *
     * @return 得分等级
     */
    public String getScoreLevel(String projectId, String studentId, Target target) {
        Document query = doc("project", projectId)
                .append("range", range2Doc(Range.student(studentId)))
                .append("target", target2Doc(target));

        Document result = scoreDatabase.getCollection("score_rate").find(query).first();
        if (result != null) {
            return result.getString("scoreLevel");
        } else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<Document> getScoreLevelRate(String projectId, Range range, Target target) {
        Document query = doc("project", projectId)
                .append("range", range2Doc(range)).append("target", target2Doc(target));

        Document doc = scoreDatabase.getCollection("score_level_map").find(query).first();
        List<Document> scoreLevels;
        if (doc != null) {
            scoreLevels = (List<Document>) doc.get("scoreLevels");
        } else {
            scoreLevels = Collections.emptyList();
        }

        Map<String, Document> levelMap = CollectionUtil.toMap(scoreLevels, docm -> docm.getString("scoreLevel"));
        List<Document> fullScoreLevels = new ArrayList<>();

        for (Keys.ScoreLevel scoreLevel : Keys.ScoreLevel.values()) {
            if (!levelMap.containsKey(scoreLevel.name())) {
                fullScoreLevels.add(
                        doc("scoreLevel", scoreLevel.name()).append("count", 0).append("rate", 0.0));
            } else {
                fullScoreLevels.add(levelMap.get(scoreLevel.name()));
            }
        }

        return fullScoreLevels;
    }
}
