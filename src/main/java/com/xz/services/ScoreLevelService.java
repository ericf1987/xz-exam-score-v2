package com.xz.services;

import com.mongodb.client.MongoDatabase;
import com.xz.bean.ProjectConfig;
import com.xz.bean.Range;
import com.xz.bean.Target;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
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

    public Map<String, Double> getScoreLevelRate(String projectId, Range range, Target target) {

        Document query = doc("project", projectId)
                .append("range", range2Doc(range)).append("target", target2Doc(target));

        Document doc = scoreDatabase.getCollection("score_level_rate").find(query).first();

        if (doc != null) {
            Document d = (Document) doc.get("scoreLevelRate");
            Map<String, Double> result = new HashMap<>();
            for (String scoreLevel : d.keySet()) {
                result.put(scoreLevel, d.getDouble(scoreLevel));
            }
            return result;
        } else {
            return Collections.emptyMap();
        }
    }
}
