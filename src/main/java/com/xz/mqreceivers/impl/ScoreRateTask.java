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
import com.xz.util.Mongo;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;

@Component
@ReceiverInfo(taskType = "score_rate")
public class ScoreRateTask extends Receiver {

    @Autowired
    FullScoreService fullScoreService;

    @Autowired
    ScoreService scoreService;

    @Autowired
    ScoreLevelService scoreLevelService;

    @Autowired
    MongoDatabase scoreDatabase;

    @Override
    protected void runTask(AggrTask aggrTask) {
        Range range = aggrTask.getRange();

        if (range.match(Range.STUDENT)) {
            processStudentScoreRate(aggrTask);
        }
    }

    private void processStudentScoreRate(AggrTask aggrTask) {
        String projectId = aggrTask.getProjectId();
        Range range = aggrTask.getRange();
        Target target = aggrTask.getTarget();

        double score = scoreService.getScore(projectId, range, target);
        double fullScore = fullScoreService.getFullScore(projectId, target);

        if (fullScore <= 0) {
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
