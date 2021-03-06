package com.xz.examscore.services;

import com.mongodb.client.MongoDatabase;
import com.xz.ajiaedu.common.lang.CollectionUtils;
import com.xz.ajiaedu.common.mongo.DocumentUtils;
import com.xz.ajiaedu.common.report.Keys;
import com.xz.examscore.bean.ProjectConfig;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;
import static com.xz.ajiaedu.common.report.Keys.ScoreLevel.*;
import static com.xz.examscore.util.Mongo.range2Doc;
import static com.xz.examscore.util.Mongo.target2Doc;

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
     * @return 得分等级
     */
    public String calculateScoreLevel(String projectId, double scoreRate) {
        ProjectConfig projectConfig = projectConfigService.getProjectConfig(projectId);
        Map<String, Object> scoreLevels = projectConfig.getScoreLevels();

        return calculateScoreLevel0(scoreRate, scoreLevels);
    }

    public String calculateScoreLevelByScore(double score, Map<String, Object> scoreLevels){
        return calculateScoreLevel0(score, scoreLevels);
    }

    public String calculateScoreLevel0(double score, Map<String, Object> scoreLevels) {
        if (score >= Double.valueOf(scoreLevels.get(Excellent.name()).toString())) {
            return Excellent.name();
        } else if (score >= Double.valueOf(scoreLevels.get(Good.name()).toString())) {
            return Good.name();
        } else if (score >= Double.valueOf(scoreLevels.get(Pass.name()).toString())) {
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

        Map<String, Document> levelMap = CollectionUtils.toMap(scoreLevels, docm -> docm.getString("scoreLevel"));
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

    /**
     * 获取指定分数排名的人数
     *
     * @param projectId  项目ID
     * @param range      范围
     * @param target     目标
     * @param scoreLevel 分数等级参数
     * @return 人数
     */
    public int getScoreLevelCount(String projectId, Range range, Target target, String scoreLevel) {
        Document query = doc("project", projectId)
                .append("range", range2Doc(range)).append("target", target2Doc(target));
        Document doc = scoreDatabase.getCollection("score_level_map").find(query).first();
        List<Document> scoreLevels = DocumentUtils.getList(doc, "scoreLevels", Collections.emptyList());
        if (scoreLevels.isEmpty()) {
            return 0;
        } else {
            for (Document scoreLevelMap : scoreLevels) {
                if (scoreLevelMap.getString("scoreLevel").equals(scoreLevel)) {
                    return scoreLevelMap.getInteger("count");
                }
            }
            return 0;
        }
    }
}
