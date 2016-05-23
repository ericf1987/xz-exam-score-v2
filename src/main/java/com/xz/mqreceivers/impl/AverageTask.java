package com.xz.mqreceivers.impl;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.bean.Range;
import com.xz.mqreceivers.AggrTask;
import com.xz.mqreceivers.Receiver;
import com.xz.mqreceivers.ReceiverInfo;
import com.xz.services.StudentService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

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
                new Document("project", projectId)
                        .append("range", range2Doc(range))
        );

        totalScores.forEach((Consumer<Document>) document -> {
            Document query = doc("range", document.get("range"))
                    .append("target", document.get("target"))
                    .append("project", document.get("project"));

            // 计算平均分
            int studentCount = studentService.getStudentCount(projectId, range);
            double totalScore = document.getDouble("totalScore");
            double average = totalScore / studentCount;

            // 保存平均分
            averageCollection.deleteMany(query);
            averageCollection.insertOne(doc(query).append("average", average));
        });
    }
}
