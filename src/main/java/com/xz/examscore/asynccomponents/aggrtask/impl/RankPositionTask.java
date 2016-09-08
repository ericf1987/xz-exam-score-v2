package com.xz.examscore.asynccomponents.aggrtask.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.examscore.asynccomponents.aggrtask.AggrTask;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMessage;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMeta;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.RangeService;
import com.xz.examscore.services.TargetService;
import com.xz.examscore.util.Mongo;
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
@AggrTaskMeta(taskType = "rank_position")
@Component
public class RankPositionTask extends AggrTask {

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
    protected void runTask(AggrTaskMessage taskInfo) {
        String projectId = taskInfo.getProjectId();
        Target target = taskInfo.getTarget();
        Range range = taskInfo.getRange();

        //从总分表中取源数据
        MongoCollection<Document> scoreMapCollection = scoreDataBase.getCollection("score_map");
        //目的数据表
        MongoCollection<Document> rankPositionCollection = scoreDataBase.getCollection("rank_position");

        Document query = new Document("project", projectId).
                append("range", Mongo.range2Doc(range)).
                append("target", Mongo.target2Doc(target));

        List<Document> positions = new ArrayList<>();
        Document scoreMapDoc = scoreMapCollection.find(query).first();

        if (scoreMapDoc == null) {  // 可能对应的 range 考生全部没有分数
            return;
        }

        //获取总人数
        int count = scoreMapDoc.getInteger("count");
        if (count == 0) {
            return;
        }

        //获取总分表中的scoreMap节点
        List<Document> scoreMap = (List<Document>) scoreMapDoc.get("scoreMap");

        for (String s : POSITIONS) {
            //获取中位数对应的排名位置
            int[] indexs = getRankPosition(count, parsePosition(s));
            //获取排名位置对应的分数，取平均值
            double score = getRankPositionScore(scoreMap, indexs);
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

    private double getRankPositionScore(List<Document> scoreMap, int[] indexs) {
        if(scoreMap.size() == 1){
            //如果只有一个学生参考，则中位数就是得分
            return scoreMap.get(0).getDouble("score");
        }else{
            // 按照分数从高到低排序
            Collections.sort(scoreMap, (d1, d2) -> d2.getDouble("score").compareTo(d1.getDouble("score")));
            double sum = 0;
            for (int index : indexs) {
                if (index <= 0) {
                    return 0;
                }
                int counter = 0;
                for (Document item : scoreMap) {
                    counter += item.getInteger("count");
                    if (counter >= index) {
                        sum += item.getDouble("score");
                        break;
                    }
                }
            }
            return sum / 2;
        }
    }

    private int[] getRankPosition(int count, double rate){
        double r = (count + 1) * rate;
        return new int[]{
                Double.valueOf(Math.ceil(r)).intValue(),
                Double.valueOf(Math.floor(r)).intValue()
        };
    }
}
