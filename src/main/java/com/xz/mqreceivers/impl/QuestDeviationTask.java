package com.xz.mqreceivers.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.mqreceivers.AggrTask;
import com.xz.mqreceivers.Receiver;
import com.xz.mqreceivers.ReceiverInfo;
import com.xz.services.FullScoreService;
import com.xz.services.RangeService;
import com.xz.services.TargetService;
import com.xz.util.Mongo;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

import static com.xz.ajiaedu.common.mongo.MongoUtils.$set;
import static com.xz.ajiaedu.common.mongo.MongoUtils.UPSERT;

/**
 * @author by fengye on 2016/5/29.
 */
@SuppressWarnings("unchecked")
@ReceiverInfo(taskType = "quest_deviation")
@Component
public class QuestDeviationTask extends Receiver {

    static final Logger LOG = LoggerFactory.getLogger(QuestDeviationTask.class);

    @Autowired
    RangeService rangeService;

    @Autowired
    TargetService targetService;

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    FullScoreService fullScoreService;

    public static final double DEVIATION_RATE = 0.27d;

    @Override
    protected void runTask(AggrTask aggrTask) {
        //1.查询出total_score表中的总分值
        //2.查询出前27%排名和后27%排名的平均分，相减
        //3.相减后的数值/总分值
        String projectId = aggrTask.getProjectId();
        Range range = aggrTask.getRange();
        Target target = aggrTask.getTarget();

        // 无效的任务
        if (target == null || target.getId() == null) {
            LOG.error("Invalid target: " + target);
            return;
        }

        String questId = target.getId().toString();

        Document query = new Document("project", projectId).
                append("range", Mongo.range2Doc(range)).
                append("target", Mongo.target2Doc(target));

        MongoCollection<Document> scoreMapCol = scoreDatabase.getCollection("score_map");
        MongoCollection<Document> questDeviationCol = scoreDatabase.getCollection("quest_deviation");

        Document oneScoreMap = scoreMapCol.find(query).first();
        int count = oneScoreMap.getInteger("count");
        //排名27%所占的人数
        int rankCount = (int) Math.ceil(count * DEVIATION_RATE);
        //题目总分
        double score = fullScoreService.getFullScore(projectId, target);

        //double score = getScore(oneScoreMap);
        double subScore = getSubScoreByRate(oneScoreMap, rankCount);
        double deviation = subScore / score;

        questDeviationCol.deleteMany(query);
        questDeviationCol.updateMany(
                new Document("project", projectId).
                        append("range", Mongo.range2Doc(range)).
                        append("quest", questId),
                $set("deviation", deviation),
                UPSERT
        );
    }

    private double getSubScoreByRate(Document oneScoreMap, int rankCount) {
        List<Document> scoreMap = (List<Document>) oneScoreMap.get("scoreMap");
        double top = average(scoreMap, rankCount, false);
        double bottom = average(scoreMap, rankCount, true);
        //return Math.abs(top - bottom);
        return top - bottom;
    }

    private double average(List<Document> scoreMap, int rankCount, boolean asc) {
        int count = 0;
        double sum = 0;
        if (asc) {
            //从低到高
            Collections.sort(scoreMap, (Document d1, Document d2) -> d1.getDouble("score").compareTo(d2.getDouble("score")));
            //LOG.debug("排名人数-->{}, 从低到高-->{}", rankCount, scoreMap.toString());
        } else {
            //从高到低
            Collections.sort(scoreMap, (Document d1, Document d2) -> d2.getDouble("score").compareTo(d1.getDouble("score")));
            //LOG.debug("排名人数-->{}, 从高到低-->{}", rankCount, scoreMap.toString());
        }
        for (Document d : scoreMap) {
            count += d.getInteger("count");
            sum += d.getDouble("score") * d.getInteger("count");
            if (count >= rankCount) {
                count = count - d.getInteger("count");
                int offset = rankCount - count;
                sum = sum - d.getDouble("score") * d.getInteger("count") + d.getDouble("score") * offset;
                return rankCount == 0 ? 0d : sum / (double) rankCount;
            }
        }
        return rankCount == 0 ? 0d : sum / (double) rankCount;
    }
}
