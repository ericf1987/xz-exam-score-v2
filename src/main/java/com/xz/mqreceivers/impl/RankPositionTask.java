package com.xz.mqreceivers.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.mqreceivers.AggrTask;
import com.xz.mqreceivers.Receiver;
import com.xz.mqreceivers.ReceiverInfo;
import com.xz.services.RangeService;
import com.xz.services.TargetService;
import com.xz.util.Mongo;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.xz.ajiaedu.common.mongo.MongoUtils.$set;
import static com.xz.ajiaedu.common.mongo.MongoUtils.UPSERT;

/**
 * @author by fengye on 2016/5/27.
 */
@ReceiverInfo(taskType = "rank_position")
@Component
public class RankPositionTask extends Receiver {

    static final Logger LOG = LoggerFactory.getLogger(RankPositionTask.class);

    @Autowired
    RangeService rangeService;

    @Autowired
    TargetService targetService;

    @Autowired
    MongoDatabase scoreDataBase;

    //统计指标
    static final String[] POSITIONS = new String[]{"1/4", "2/4", "3/4"};

    @SuppressWarnings("unchecked")
    @Override
    protected void runTask(AggrTask aggrTask) {
        String projectId = aggrTask.getProjectId();
        Target target = aggrTask.getTarget();
        Range range = aggrTask.getRange();

        //从总分表中取源数据
        MongoCollection<Document> scoreMapCollection = scoreDataBase.getCollection("score_map");
        //目的数据表
        MongoCollection<Document> rankPositionCollection = scoreDataBase.getCollection("rank_position");

        Document query = new Document("project", projectId).
                append("range", Mongo.range2Doc(range)).
                append("target", Mongo.target2Doc(target));

        List<Document> positions = new ArrayList<>();
        Document scoreMapDoc = scoreMapCollection.find(query).first();

        //获取总人数
        int count = scoreMapDoc.getInteger("count");
        if (count == 0) {
            LOG.error("没有排名信息：" + query.toJson());
        }

        //获取总分表中的scoreMap节点
        List<Document> scoreMap = (List<Document>) scoreMapDoc.get("scoreMap");

        for (String s : POSITIONS) {
            double score = getScoreAtIndex(scoreMap, count, parsePosition(s));
            Document position = new Document("position", parsePosition(s)).append("score", score);
            positions.add(position);
        }

        rankPositionCollection.deleteMany(query);
        rankPositionCollection.updateMany(query, $set("positions", positions), UPSERT);
    }

    private Double parsePosition(String positionName) {
        String[] arr = positionName.split("/");
        return Double.valueOf(arr[0]) / Double.valueOf(arr[1]);
    }

    private double getScoreAtIndex(List<Document> scoreMap, int count, double rate) {

        // 按照分数倒排 map 元素
        Collections.sort(scoreMap, (d1, d2) -> d2.getDouble("score").compareTo(d1.getDouble("score")));

        // 极端情况下取一个人
        int index = Double.valueOf(count * rate).intValue();
        if (index <= 1) {
            return scoreMap.get(0).getDouble("score");
        }

        double score = 0;
        int counter = 0;

        for (Document item : scoreMap) {
            counter += ((Number) item.get("count")).intValue();
            if (counter >= index) {
                return item.getDouble("score");
            }
            score = item.getDouble("score");
        }

        return score;
    }
}
