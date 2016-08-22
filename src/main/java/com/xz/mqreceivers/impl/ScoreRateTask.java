package com.xz.mqreceivers.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.mqreceivers.AggrTask;
import com.xz.mqreceivers.Receiver;
import com.xz.mqreceivers.ReceiverInfo;
import com.xz.services.FullScoreService;
import com.xz.services.ScoreLevelService;
import com.xz.services.ScoreService;
import com.xz.services.TargetService;
import com.xz.util.Mongo;
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
@ReceiverInfo(taskType = "score_rate")
public class ScoreRateTask extends Receiver {

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
    protected void runTask(AggrTask aggrTask) {
        String projectId = aggrTask.getProjectId();
        Range range = aggrTask.getRange();

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
