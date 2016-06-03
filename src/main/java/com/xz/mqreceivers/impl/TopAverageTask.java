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
 * Created by fengye on 2016/5/25.
 */
@ReceiverInfo(taskType = "top_average")
@Component
public class TopAverageTask extends Receiver{
    @Autowired
    RangeService rangeService;
    @Autowired
    TargetService targetService;
    @Autowired
    MongoDatabase scoreDatabase;

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

        //获取前30%,40%,50%排名的用户
        double arr[] = new double[]{0.3};
        //将总分更新至top_average表
        List<Document> resultList = new ArrayList<Document>();
        //对不同百分率进行便利查询
        for(double d : arr){
            //总人数
            int count = scoreMap.getInteger("count").intValue();
            List<Document> items = getPercent((List<Document>) scoreMap.get("scoreMap"), d, count);
            //获取总分
            double sum = getSum(items);
            //计算平均分
            double aver = Double.valueOf(sum / count);
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

    private double getSum(List<Document> items) {
        double sum = 0d;
        for(Document d : items){
            sum += d.getDouble("score");
        }
        return sum;
    }

    private List<Document> getPercent(List<Document> scoreMaps, double v, int count) {
        //排序
        List<Document> result = new ArrayList<Document>();
        Collections.sort(scoreMaps, (Document d1, Document d2) -> {
            return d2.getDouble("score").compareTo(d1.getDouble("score"));
        });

        //System.out.println("排序后数据集合大小：" + scoreMaps.size() + ",排序后数据集合数据：" + scoreMaps.toString());

        int requireCount = Double.valueOf(count * v).intValue();
        //如果元素总数小于1
        if(requireCount <= 1){
            return scoreMaps;
        }

        int currentCount = 0;
        for(int i = 0; i < scoreMaps.size(); i++){
            currentCount += scoreMaps.get(i).getInteger("count");
            if(currentCount > requireCount){
                Document doc = scoreMaps.get(i);
                doc.put("count", doc.getInteger("count") - (currentCount - requireCount));
                result = scoreMaps.subList(0, i);
                //System.out.println("结果数据集合大小" + result.size() + ",结果数据：" + result.toString());
                return result;
            }
        }
        return scoreMaps;
    }

}
