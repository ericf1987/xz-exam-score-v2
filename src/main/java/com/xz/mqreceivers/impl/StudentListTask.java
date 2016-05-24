package com.xz.mqreceivers.impl;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.mqreceivers.AggrTask;
import com.xz.mqreceivers.Receiver;
import com.xz.mqreceivers.ReceiverInfo;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.function.Consumer;

import static com.xz.ajiaedu.common.mongo.MongoUtils.*;

/**
 * 统计学生列表
 */
@ReceiverInfo(taskType = "student_list")
@Component
public class StudentListTask extends Receiver {

    @Autowired
    MongoDatabase scoreDatabase;

    @Override
    public void runTask(AggrTask aggrTask) {
        String projectId = aggrTask.getProjectId();
        MongoCollection<Document> stuListCollection = scoreDatabase.getCollection("student_list");
        AggregateIterable<Document> aggregate = aggregateStudentList(projectId);
        saveNewData(projectId, stuListCollection, aggregate);
    }

    private void saveNewData(
            String projectId, MongoCollection<Document> stuListCollection, AggregateIterable<Document> aggregate) {

        aggregate.forEach((Consumer<Document>) document -> {
            Document resultId = (Document) document.get("_id");
            String studentId = resultId.getString("student");
            stuListCollection.updateMany(
                    doc("project", projectId).append("student", studentId),
                    $set("subjects", document.get("subjects"))
            );
        });
    }

    // 统计学生列表
    private AggregateIterable<Document> aggregateStudentList(String projectId) {
        MongoCollection<Document> scoreCollection = scoreDatabase.getCollection("score");
        return scoreCollection
                .aggregate(Arrays.asList(
                        $match("project", projectId),
                        $group(doc("_id", getGroupId()).append("subjects", $addToSet("$subject")))
                ));
    }

    private Document getGroupId() {
        return new Document("province", "$province")
                .append("city", "$city").append("area", "$area").append("school", "$school")
                .append("class", "$class").append("student", "$student");
    }
}
