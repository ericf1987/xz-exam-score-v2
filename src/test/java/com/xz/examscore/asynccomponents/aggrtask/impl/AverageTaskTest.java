package com.xz.examscore.asynccomponents.aggrtask.impl;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMessage;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import org.bson.Document;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.function.Consumer;

import static com.xz.examscore.util.Mongo.range2Doc;

/**
 * (description)
 * created at 16/05/16
 *
 * @author yiding_he
 */
public class AverageTaskTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    AverageTask averageTask;

    @Autowired
    MongoDatabase scoreDatabase;

    @Test
    public void testRunTask() throws Exception {
        averageTask.runTask(new AggrTaskMessage("430200-b73f03af1d74484f84f1aa93f583caaa", "1111", "average")
                .setTarget(Target.point("1025605"))
                .setRange(Range.school("200f3928-a8bd-48c4-a2f4-322e9ffe3700")));
    }

    @Test
    public void testCalculateAverage() throws Exception {
        String project = "430600-7d752ed75272481ebe035b896874194e";
        Document range = new Document("name", "class").append("id", "4fbe3a69-bd50-488f-a4f6-a4c2d98f932e");
        Document target = new Document("name", "subject").append("id", "003");
        double average = averageTask.calculateAverage(project, range, target, 3461.5d, new Document());
        System.out.println(average);
    }

    @Test
    public void test(){
        MongoCollection<Document> totalScoreCollection = scoreDatabase.getCollection("total_score");
        MongoCollection<Document> averageCollection = scoreDatabase.getCollection("average");
        String projectId = "430100-a05db0d05ad14010a5c782cd31c0283f";
        Range classRange = Range.clazz("a1895cd9-d82c-4b12-a698-164fb5ceb1f3");

        FindIterable<Document> totalScores = totalScoreCollection.find(
                new Document("project", projectId).append("range", range2Doc(classRange)));

        totalScores.forEach((Consumer<Document>) document -> {
            Document targetDoc = (Document) document.get("target");

        });
    }
}