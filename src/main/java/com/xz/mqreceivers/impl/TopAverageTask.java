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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * (description)
 * created at 2016/5/25
 *
 * @author fengye
 */
@ReceiverInfo(taskType = "top_average")
@Component
public class TopAverageTask extends Receiver {

    @Autowired
    RangeService rangeService;

    @Autowired
    TargetService targetService;

    @Autowired
    MongoDatabase scoreDatabase;

    @SuppressWarnings("unchecked")
    @Override
    protected void runTask(AggrTask aggrTask) {
        String projectId = aggrTask.getProjectId();
        Range range = aggrTask.getRange();
        Target target = aggrTask.getTarget();

        MongoCollection<Document> scoreCol = scoreDatabase.getCollection("score_map");
        MongoCollection<Document> top_averageCol = scoreDatabase.getCollection("top_average");

        Document query = new Document("project", projectId).
                append("range", Mongo.range2Doc(range)).
                append("target", Mongo.target2Doc(target));

        Document scoreMap = scoreCol.find(query).first();
        if (scoreMap == null) { // 可能整个班级/学校没有参加考试
            return;
        }

        //获取前30%,40%,50%排名的用户
        double arr[] = new double[]{0.3};
        //将总分更新至top_average表
        List<Document> resultList = new ArrayList<>();
        //对不同百分率进行便利查询
        for (double d : arr) {
            int count = scoreMap.getInteger("count");
            double aver = getAverage((List<Document>) scoreMap.get("scoreMap"), d, count);
            Document result = new Document("average", aver).append("percent", d);
            resultList.add(result);
        }
        top_averageCol.deleteMany(query);
        //查询该平均分统计项是否存在
        top_averageCol.updateMany(
                query,
                MongoUtils.$set("topAverages", resultList),
                MongoUtils.UPSERT
        );
    }

    private double getAverage(List<Document> scoreMaps, double v, int count) {
        //排序
        int cnt = 0;
        double sum = 0;
        Collections.sort(scoreMaps, (d1, d2) -> d2.getDouble("score").compareTo(d1.getDouble("score")));
        int requireCount = Double.valueOf(count * v).intValue();

        if (requireCount == 0) {
            return 0d;
        } else if (requireCount > 0 && requireCount <= 1) {
            return scoreMaps.get(0).getDouble("score");
        } else {
            for (Document d : scoreMaps) {
                cnt += d.getInteger("count");
                sum += d.getDouble("score") * d.getInteger("count");
                if (cnt >= requireCount) {
                    cnt = cnt - d.getInteger("count");
                    int offset = requireCount - cnt;
                    sum = sum - d.getDouble("score") * d.getInteger("count") + d.getDouble("score") * offset;
                    return requireCount == 0 ? 0d : sum / (double) requireCount;
                }
            }
            return requireCount == 0 ? 0d : sum / (double) requireCount;
        }
    }

}
