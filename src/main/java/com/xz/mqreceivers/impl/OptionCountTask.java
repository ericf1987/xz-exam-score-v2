package com.xz.mqreceivers.impl;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.mqreceivers.AggrTask;
import com.xz.mqreceivers.Receiver;
import com.xz.mqreceivers.ReceiverInfo;
import com.xz.services.StudentService;
import com.xz.util.Mongo;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.function.Consumer;

import static com.xz.ajiaedu.common.mongo.MongoUtils.*;

@Component
@ReceiverInfo(taskType = "option_count")
public class OptionCountTask extends Receiver {

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    StudentService studentService;

    @Override
    protected void runTask(AggrTask aggrTask) {
        String projectId = aggrTask.getProjectId();
        String schoolId = aggrTask.getRange().getId();
        String questId = aggrTask.getTarget().getId().toString();

        MongoCollection<Document> score = scoreDatabase.getCollection("score");
        MongoCollection<Document> optionCount = scoreDatabase.getCollection("option_count");

        processClasses(projectId, schoolId, questId, score, optionCount);
        processSchool(projectId, schoolId, questId, score, optionCount);
    }

    private void processClasses(
            String projectId, String schoolId, String questId,
            MongoCollection<Document> score, MongoCollection<Document> optionCount) {

        AggregateIterable<Document> aggregate = score.aggregate(Arrays.asList(
                $match(doc("project", projectId).append("quest", questId).append("school", schoolId)),
                $group(doc("_id", doc("class", "$class").append("answer", "$answer")).append("count", doc("$sum", 1)))
        ));

        aggregate.forEach((Consumer<Document>) document -> {
            String classId = ((Document) document.get("_id")).getString("class");
            String answer = ((Document) document.get("_id")).getString("answer");

            int count = document.getInteger("count");
            int studentCount = studentService.getStudentCount(projectId, Range.clazz(classId));
            double rate = (double) count / studentCount;

            Document query = doc("project", projectId)
                    .append("range", Mongo.range2Doc(Range.clazz(classId)))
                    .append("target", Mongo.target2Doc(Target.quest(questId)))
                    .append("answer", answer);

            optionCount.deleteMany(query);
            optionCount.insertOne(doc(query).append("count", count).append("rate", rate));
        });
    }

    private void processSchool(
            String projectId, String schoolId, String questId,
            MongoCollection<Document> score, MongoCollection<Document> optionCount) {

        AggregateIterable<Document> aggregate = score.aggregate(Arrays.asList(
                $match(doc("project", projectId).append("quest", questId).append("school", schoolId)),
                $group(doc("_id", doc("answer", "$answer")).append("count", doc("$sum", 1)))
        ));

        aggregate.forEach((Consumer<Document>) document -> {
            String answer = ((Document) document.get("_id")).getString("answer");

            int count = document.getInteger("count");
            int studentCount = studentService.getStudentCount(projectId, Range.school(schoolId));
            double rate = (double) count / studentCount;

            Document query = doc("project", projectId)
                    .append("range", Mongo.range2Doc(Range.school(schoolId)))
                    .append("target", Mongo.target2Doc(Target.quest(questId)))
                    .append("answer", answer);

            optionCount.deleteMany(query);
            optionCount.insertOne(doc(query).append("count", count).append("rate", rate));
        });
    }
}
