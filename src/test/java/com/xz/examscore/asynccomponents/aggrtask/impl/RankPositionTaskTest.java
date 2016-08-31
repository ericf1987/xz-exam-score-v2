package com.xz.examscore.asynccomponents.aggrtask.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMessage;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.TargetService;
import com.xz.examscore.util.Mongo;
import org.bson.Document;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;

/**
 * (description)
 * created at 16/06/06
 *
 * @author yiding_he
 */
public class RankPositionTaskTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    RankPositionTask rankPositionTask;

    @Autowired
    MongoDatabase scoreDataBase;

    @Autowired
    TargetService targetService;

    @Test
    public void testRankPosition() throws Exception {
/*        rankPositionTask.runTask(new AggrTaskMessage("431100-903288f61a5547f1a08a7e20420c4e9e", "111", "rank_position")
                .setRange(Range.clazz("8918be00-4a5c-4f0d-bb3d-8ffa706a1891")).setTarget(Target.subject("001")));*/
        double[] dd = new double[]{0.25, 0.5, 0.75};
        String projectId = "431100-903288f61a5547f1a08a7e20420c4e9e";
        Range classRange = Range.clazz("8918be00-4a5c-4f0d-bb3d-8ffa706a1891");
        Target target = targetService.getTarget(projectId, null);
        Document query = doc("project", projectId).append("range", Mongo.range2Doc(classRange)).append("target", Mongo.target2Doc(target));
        MongoCollection<Document> collection = scoreDataBase.getCollection("score_map");
        Document scoreMapDoc = collection.find(query).first();
        List<Document> scoreMap = (List<Document>) scoreMapDoc.get("scoreMap");
        Collections.sort(scoreMap, (d1, d2) -> d2.getDouble("score").compareTo(d1.getDouble("score")));
        System.out.println(scoreMap.toString());
        int count = scoreMapDoc.getInteger("count");
        for (double d : dd){
            double score = getRankPositionScore(scoreMap, getRankPosition(count, d));
            System.out.println(d + "-->" + score + ", count-->" + count);
        }
    }

    private double getRankPositionScore(List<Document> scoreMap, int[] indexs) {
        // 按照分数从高到低排序
        Collections.sort(scoreMap, (d1, d2) -> d2.getDouble("score").compareTo(d1.getDouble("score")));
        double sum = 0;
        for(int index : indexs){
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

    private int[] getRankPosition(int count, double rate){
        double r = (count + 1) * rate;
        return new int[]{
                Double.valueOf(Math.ceil(r)).intValue(),
                Double.valueOf(Math.floor(r)).intValue()
        };
    }
}