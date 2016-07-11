package com.xz.mqreceivers.impl;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.ajiaedu.common.mongo.MongoUtils;
import com.xz.bean.Range;
import com.xz.mqreceivers.AggrTask;
import com.xz.mqreceivers.Receiver;
import com.xz.mqreceivers.ReceiverInfo;
import com.xz.services.StudentService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

import static com.xz.ajiaedu.common.mongo.MongoUtils.$set;
import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;
import static com.xz.util.Mongo.range2Doc;

/**
 * 执行平均分统计
 */
@Component
@ReceiverInfo(taskType = "average")
public class AverageTask extends Receiver {

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    StudentService studentService;

    @Override
    public void runTask(AggrTask aggrTask) {
        String projectId = aggrTask.getProjectId();
        Range range = aggrTask.getRange();

        // 根据 aggrTask 来遍历 total_score 集合中的记录，对每条记录计算平均分，再写回记录当中
        MongoCollection<Document> totalScoreCollection = scoreDatabase.getCollection("total_score");
        MongoCollection<Document> averageCollection = scoreDatabase.getCollection("average");

        FindIterable<Document> totalScores = totalScoreCollection.find(
                new Document("project", projectId).append("range", range2Doc(range)));

        totalScores.forEach((Consumer<Document>) document -> {
            Document rangeDoc = (Document) document.get("range");
            Document targetDoc = (Document) document.get("target");
            double totalScore = document.getDouble("totalScore");

            Document query = doc();
            double average = calculateAverate(projectId, rangeDoc, targetDoc, totalScore, query);

            // 保存平均分
            averageCollection.updateOne(query, $set("average", average), MongoUtils.UPSERT);
        });
    }

    protected double calculateAverate(
            String projectId, Document rangeDoc, Document targetDoc, double totalScore, Document query) {

        query.append("range", rangeDoc)
                .append("target", targetDoc)
                .append("project", projectId);

        // 计算平均分
        int studentCount = studentService.getStudentCount(projectId, Range.fromDocument(rangeDoc));
        double average;

        if (studentCount == 0) {
            average = 0;
        } else {
            average = totalScore / studentCount;
        }

        return average;
    }
}
