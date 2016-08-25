package com.xz.examscore.asynccomponents.aggrtask.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.examscore.asynccomponents.aggrtask.AggrTask;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMessage;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMeta;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.FullScoreService;
import com.xz.examscore.services.ScoreLevelService;
import com.xz.examscore.services.ScoreService;
import com.xz.examscore.services.TargetService;
import com.xz.examscore.util.Mongo;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

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

    @Override
    protected void runTask(AggrTaskMessage taskInfo) {
        String projectId = taskInfo.getProjectId();
        Range range = taskInfo.getRange();

        List<Target> targets = targetService.queryTargets(projectId, Target.PROJECT, Target.SUBJECT);

        for (Target target : targets) {
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

        Document query = doc("project", projectId)
                .append("range", Mongo.range2Doc(range))
                .append("target", Mongo.target2Doc(target));

        MongoCollection<Document> collection = scoreDatabase.getCollection("score_rate");
        collection.deleteMany(query);
        collection.insertOne(doc(query).append("scoreRate", scoreRate).append("scoreLevel", scoreLevel));
    }
}
