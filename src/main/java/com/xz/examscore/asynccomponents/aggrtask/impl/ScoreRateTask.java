package com.xz.examscore.asynccomponents.aggrtask.impl;

import com.hyd.simplecache.utils.MD5;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.examscore.asynccomponents.aggrtask.AggrTask;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMessage;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMeta;
import com.xz.examscore.bean.ProjectConfig;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.*;
import com.xz.examscore.util.Mongo;
import org.apache.commons.collections.MapUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;

/**
 * 得分率/得分等级
 */
@Component
@AggrTaskMeta(taskType = "score_rate")
public class ScoreRateTask extends AggrTask {

    static final Logger LOG = LoggerFactory.getLogger(ScoreRateTask.class);

    @Autowired
    FullScoreService fullScoreService;

    @Autowired
    ScoreService scoreService;

    @Autowired
    ScoreLevelService scoreLevelService;

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    TargetService targetService;

    @Autowired
    ProjectConfigService projectConfigService;

    @Override
    protected void runTask(AggrTaskMessage taskInfo) {
        String projectId = taskInfo.getProjectId();
        Range range = taskInfo.getRange();

        List<Target> targets = targetService.queryTargets(projectId, Target.PROJECT, Target.SUBJECT, Target.SUBJECT_COMBINATION);

        ProjectConfig projectConfig = projectConfigService.getProjectConfig(projectId);

        for (Target target : targets) {
            doProcessStudentScoreRate(projectId, range, target, projectConfig);
        }
    }

    private void doProcessStudentScoreRate(String projectId, Range range, Target target, ProjectConfig projectConfig) {
        String scoreLevelConfig = projectConfig.getScoreLevelConfig();
        if(scoreLevelConfig.equals("score")){
            processStudentScoreRate2(projectId, range, target, projectConfig.getScoreLevels());
        }else if(scoreLevelConfig.equals("rate")){
            processStudentScoreRate(projectId, range, target);
        }
    }

    private void processStudentScoreRate(String projectId, Range range, Target target) {

        double score = scoreService.getScore(projectId, range, target);
        double fullScore = fullScoreService.getFullScore(projectId, target);

        if (fullScore <= 0) {
            LOG.warn("满分为零分：" + target);
            return;
        }

        double scoreRate = score / fullScore;
        String scoreLevel = scoreLevelService.calculateScoreLevel(projectId, scoreRate);

        saveScoreRate(projectId, range, target, scoreRate, scoreLevel);
    }

    private void processStudentScoreRate2(String projectId, Range range, Target target, Map<String, Object> scoreLevels){
        if(target.match(Target.PROJECT) || target.match(Target.SUBJECT)){
            String targetId = target.match(Target.PROJECT) ? "000" : target.getId().toString();
            Map<String, Object> scoreLevel = MapUtils.getMap(scoreLevels, targetId);
            double score = scoreService.getScore(projectId, range, target);
            double fullScore = fullScoreService.getFullScore(projectId, target);

            if (fullScore <= 0) {
                LOG.warn("满分为零分：" + target);
                return;
            }

            double scoreRate = score / fullScore;
            String scoreLevelValue = scoreLevelService.calculateScoreLevelByScore(score, scoreLevel);
            saveScoreRate(projectId, range, target, scoreRate, scoreLevelValue);
        }
    }

    private void saveScoreRate(String projectId, Range range, Target target, double scoreRate, String scoreLevel) {
        Document query = doc("project", projectId)
                .append("range", Mongo.range2Doc(range))
                .append("target", Mongo.target2Doc(target));

        MongoCollection<Document> collection = scoreDatabase.getCollection("score_rate");
        collection.deleteMany(query);
        collection.insertOne(doc(query).append("scoreRate", scoreRate).append("scoreLevel", scoreLevel).append("md5", MD5.digest(UUID.randomUUID().toString())));
    }
}
