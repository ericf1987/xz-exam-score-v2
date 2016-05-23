package com.xz.mqreceivers.impl;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.bean.Range;
import com.xz.mqreceivers.AggrTask;
import com.xz.mqreceivers.Receiver;
import com.xz.mqreceivers.ReceiverInfo;
import com.xz.services.StudentService;
import com.xz.util.Mongo;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

import static com.xz.ajiaedu.common.mongo.MongoUtils.$set;
import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;

/**
 * 执行平均分统计
 */
@Component
@ReceiverInfo(taskType = "average")
public class AverageTask extends Receiver {

    static final Logger LOG = LoggerFactory.getLogger(AverageTask.class);

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

        FindIterable<Document> totalScores = totalScoreCollection.find(
                new Document("project", projectId)
                        .append("range", Mongo.range2Doc(range))
        );

        totalScores.forEach((Consumer<Document>) document -> {
            ObjectId _id = document.getObjectId("_id");

            // 计算平均分
            int studentCount = studentService.getStudentCount(projectId, range);
            double totalScore = document.getDouble("totalScore");
            double average = totalScore / studentCount;

            // 保存平均分
            totalScoreCollection.updateOne(doc("_id", _id), $set("average", average));
        });
    }
}
