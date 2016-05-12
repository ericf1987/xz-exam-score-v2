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

@ReceiverInfo(taskType = "student_list")
@Component
public class StudentListTask extends Receiver {

    @Autowired
    MongoDatabase scoreDatabase;

    @Override
    public void runTask(AggrTask aggrTask) {
        String projectId = aggrTask.getProjectId();

        MongoCollection<Document> stuListCollection = scoreDatabase.getCollection("student_list");
        deleteOldData(projectId, stuListCollection);

        AggregateIterable<Document> aggregate = createAggregation(projectId);
        saveNewData(projectId, stuListCollection, aggregate);
    }

    private void saveNewData(
            String projectId, MongoCollection<Document> stuListCollection, AggregateIterable<Document> aggregate) {

        aggregate.forEach((Consumer<Document>) document -> {
            Document resultId = (Document) document.get("_id");
            resultId.append("project", projectId);
            resultId.append("subjects", document.get("subjects"));
            stuListCollection.insertOne(resultId);
        });
    }

    private AggregateIterable<Document> createAggregation(String projectId) {
        MongoCollection<Document> scoreCollection = scoreDatabase.getCollection("score");
        return scoreCollection
                .aggregate(Arrays.asList(
                        new Document("$match", new Document("project", projectId)),
                        new Document("$group", new Document("_id", getGroupId())
                                .append("subjects", new Document("$addToSet", "$subject")))
                ));
    }

    private void deleteOldData(String projectId, MongoCollection<Document> stuListCollection) {
        stuListCollection.deleteMany(new Document("project", projectId));
    }

    private Document getGroupId() {
        return new Document("province", "$province")
                .append("city", "$city").append("area", "$area").append("school", "$school")
                .append("class", "$class").append("student", "$student");
    }
}
