package com.xz.mqreceivers.impl;

import com.mongodb.client.AggregateIterable;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static com.xz.ajiaedu.common.mongo.MongoUtils.*;
import static com.xz.util.Mongo.UPSERT;
import static com.xz.util.Mongo.range2Doc;

@Component
@ReceiverInfo(taskType = "option_map")
public class OptionMapTask extends Receiver {

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    StudentService studentService;

    @Override
    protected void runTask(AggrTask aggrTask) {
        String projectId = aggrTask.getProjectId();
        String questId = aggrTask.getTarget().getId().toString();

        MongoCollection<Document> scoreCollection = scoreDatabase.getCollection("score");
        MongoCollection<Document> optionMapCollection = scoreDatabase.getCollection("option_map");

        processRange(projectId, questId, aggrTask.getRange(), scoreCollection, optionMapCollection);
    }

    private void processRange(
            String projectId, String questId, Range range,
            MongoCollection<Document> scoreCollection, MongoCollection<Document> optionMapCollection) {

        AggregateIterable<Document> aggregate = scoreCollection.aggregate(Arrays.asList(
                $match(doc("project", projectId).append("quest", questId)
                        .append(range.getName(), range.getId())),
                $group(doc("_id", doc("answer", "$answer")).append("count", doc("$sum", 1)))
        ));

        List<Document> optionMapList = new ArrayList<>();

        aggregate.forEach((Consumer<Document>) document -> {
            String rangeId = ((Document) document.get("_id")).getString(range.getName());
            String answer = ((Document) document.get("_id")).getString("answer");

            int count = document.getInteger("count");
            int studentCount = studentService.getStudentCount(projectId, Range.clazz(rangeId));
            double rate = (double) count / studentCount;

            addUpToList(optionMapList, answer, studentCount, rate);
        });

        Document query = doc("project", projectId).append("range", range2Doc(range)).append("quest", questId);
        optionMapCollection.updateOne(query, $set("optionMap", optionMapList), UPSERT);
    }

    private void addUpToList(List<Document> optionMapList, String answer, int studentCount, double rate) {
        for (Document document : optionMapList) {
            if (document.getString("answer").equals(answer)) {
                document.put("count", studentCount);
                document.put("rate", rate);
                return;
            }
        }

        optionMapList.add(doc("answer", answer).append("count", studentCount).append("rate", rate));
    }
}
