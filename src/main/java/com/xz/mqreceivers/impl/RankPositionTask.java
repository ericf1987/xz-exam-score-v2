package com.xz.mqreceivers.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.ajiaedu.common.mongo.MongoUtils;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.mqreceivers.AggrTask;
import com.xz.mqreceivers.Receiver;
import com.xz.mqreceivers.ReceiverInfo;
import com.xz.services.RangeService;
import com.xz.services.TargetService;
import com.xz.util.Mongo;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.print.Doc;
import javax.print.attribute.IntegerSyntax;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author by fengye on 2016/5/27.
 */
@ReceiverInfo(taskType = "rank_position")
@Component
public class RankPositionTask extends Receiver{

    @Autowired
    RangeService rangeService;

    @Autowired
    TargetService targetService;

    @Autowired
    MongoDatabase scoreDataBase;

    //统计指标
    static final String [] POSITIONS = new String[]{"1/4","2/4","3/4"};

    @Override
    protected void runTask(AggrTask aggrTask) {
        //获取
        String projectId = aggrTask.getProjectId();
        Range range = aggrTask.getRange();
        Target target = aggrTask.getTarget();

        //从总分表中取源数据
        MongoCollection<Document> scoreCol = scoreDataBase.getCollection("score_map");
        //目的数据表
        MongoCollection<Document> rankPositionCol = scoreDataBase.getCollection("rank_position");

        Document query = new Document("project", projectId).
                append("range", Mongo.range2Doc(range)).
                append("target", Mongo.target2Doc(target));

        List<Document> positions = new ArrayList<Document>();
        Document oneScoreMapDoc = scoreCol.find(query).first();
        //获取总人数
        int count = oneScoreMapDoc.getInteger("count");

        //获取总分表中的scoreMap节点
        List<Document> scoreMap = (List<Document>)oneScoreMapDoc.get("scoreMap");
        for(String s : POSITIONS){
            double score = getScoreAtIndex(scoreMap, count, s2d(s));
            Document position = new Document("position", s2d(s)).append("score", score);
            positions.add(position);
        }

        rankPositionCol.deleteMany(query);
        rankPositionCol.updateMany(
                query,
                MongoUtils.$set("positions", positions),
                MongoUtils.UPSERT
        );

    }

    private Double s2d(String s){
        String[] arr = s.split("/");
        Double d = Double.valueOf(arr[0])/Double.valueOf(arr[1]);
        return d;
    }

    private double getScoreAtIndex(List<Document> scoreMap, int count, double d) {
        double score = 0;
        Collections.sort(scoreMap, (Document d1, Document d2) -> {
            return d2.getDouble("score").compareTo(d1.getDouble("score"));
        });
        int index = Double.valueOf(count * d).intValue();
        //如果一个班小于4个人，做出判断
        if(index <= 1){
            return scoreMap.get(index).getDouble("score");
        }
        int oneCount = 0;
        for(Document item : scoreMap){
            oneCount += item.getInteger("count");
            if(oneCount >= index){
                return item.getDouble("score");
            }
            score = item.getDouble("score");
        }
        return score;
    }
}
