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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

/**
 * (description)
 * created at 16/05/10
 *
 * @author yiding_he
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

        MongoCollection<Document> totalScoreCollection = scoreDatabase.getCollection("total_score");

        FindIterable<Document> totalScores = totalScoreCollection.find(
                new Document("project", projectId)
                        .append("range.name", range.getName())
                        .append("range.id", range.getId())
        );

        totalScores.forEach((Consumer<Document>) document -> {
            int studentCount = studentService.getStudentCount(projectId, range);
            double totalScore = document.getDouble("totalScore");
            double average = totalScore / studentCount;
            document.put("average", average);
            totalScoreCollection.replaceOne(new Document("_id", document.get("_id")), document);
        });
    }
}
