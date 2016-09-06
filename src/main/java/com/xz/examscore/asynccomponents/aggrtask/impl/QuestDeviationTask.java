package com.xz.examscore.asynccomponents.aggrtask.impl;

import com.hyd.simplecache.utils.MD5;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import com.xz.examscore.asynccomponents.aggrtask.AggrTask;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMessage;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMeta;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.FullScoreService;
import com.xz.examscore.services.RangeService;
import com.xz.examscore.services.TargetService;
import com.xz.examscore.util.Mongo;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.xz.ajiaedu.common.mongo.MongoUtils.$set;
import static com.xz.ajiaedu.common.mongo.MongoUtils.UPSERT;
import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;

/**
 * @author by fengye on 2016/5/29.
 */
@SuppressWarnings("unchecked")
@AggrTaskMeta(taskType = "quest_deviation")
@Component
public class QuestDeviationTask extends AggrTask {

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
    protected void runTask(AggrTaskMessage taskInfo) {
        //1.查询出total_score表中的总分值
        //2.查询出前27%排名和后27%排名的平均分，相减
        //3.相减后的数值/总分值
        String projectId = taskInfo.getProjectId();
        Range range = taskInfo.getRange();
        Target target = taskInfo.getTarget();

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
        if (oneScoreMap == null) {  // 可能对应的 range 考生全部没有分数
            return;
        }

        int count = oneScoreMap.getInteger("count");
        //排名27%所占的人数
        int rankCount = (int) Math.ceil(count * DEVIATION_RATE);
        //题目总分
        double score = fullScoreService.getFullScore(projectId, target);

        //double score = getScore(oneScoreMap);
        double subScore = getSubScoreByRate(oneScoreMap, rankCount);
        double deviation = subScore / score;

        questDeviationCol.deleteMany(query);
        UpdateResult result = questDeviationCol.updateMany(
                new Document("project", projectId).
                        append("range", Mongo.range2Doc(range)).
                        append("quest", questId),
                $set(doc("deviation", deviation))
        );
        if(result.getMatchedCount() == 0){
            questDeviationCol.insertOne(
                    new Document("project", projectId)
                            .append("range", Mongo.range2Doc(range))
                            .append("quest", questId).append("deviation", deviation)
                    .append("deviation", deviation).append("md5", MD5.digest(UUID.randomUUID().toString()))
            );
        }
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
