package com.xz.examscore.asynccomponents.aggrtask.impl;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMessage;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.util.Mongo;
import org.bson.Document;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * (description)
 * created at 16/05/13
 *
 * @author yiding_he
 */
public class ScoreMapTaskTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    ScoreMapTask scoreMapTask;

    @Autowired
    MongoDatabase scoreDatabase;

    @Test
    public void testRunTask() throws Exception {
        scoreMapTask.runTask(
                new AggrTaskMessage(XT_PROJECT_ID, "aaaa", "score_map")
                        .setRange(Range.clazz("46d626b6-9250-4a63-9191-e790ed67a789"))
                        .setTarget(Target.project(XT_PROJECT_ID)));
    }

    @Test
    public void testRunTask1() throws Exception {
        String projectId = "430100-2c641a3e36ff492aa535da7fb4cf28cf";
        scoreMapTask.runTask(
                new AggrTaskMessage(projectId, "1111", "score_map")
                        .setRange(Range.clazz("27bb692f-a179-41b1-a57f-ab51ee42b71d"))
                        .setTarget(Target.subjectCombination("007008009"))
        );
    }

    @Test
    public void test1() throws Exception{
        String projectId = "431200-5c78e22cb1e64e4caa9583d35ad92658";
        MongoCollection<Document> collection = scoreDatabase.getCollection("score_map");
        Range range = Range.province("430000");
        Target target = Target.subject("001");
        Document first = collection.find(Mongo.query(projectId, range, target)).first();
        List<Document> scoreMap = first.get("scoreMap", List.class);


        int c = 0;
        for(Document doc : scoreMap){
            Double score = doc.getDouble("score");
            if(110 <= score && 120 > score){
                Integer count = doc.getInteger("count");
                c += count;
                System.out.println(score + "|" + count);
            }
        }

        System.out.println(c);

    }
}