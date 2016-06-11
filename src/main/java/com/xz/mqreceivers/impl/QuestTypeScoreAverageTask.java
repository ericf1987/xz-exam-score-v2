package com.xz.mqreceivers.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.bean.Range;
import com.xz.mqreceivers.AggrTask;
import com.xz.mqreceivers.Receiver;
import com.xz.mqreceivers.ReceiverInfo;
import com.xz.services.QuestTypeScoreService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.function.Consumer;

import static com.xz.ajiaedu.common.mongo.MongoUtils.*;
import static com.xz.bean.Target.questType;
import static com.xz.util.Mongo.range2Doc;
import static com.xz.util.Mongo.target2Doc;

@Component
@ReceiverInfo(taskType = "quest_type_score_average")
public class QuestTypeScoreAverageTask extends Receiver {

    @Autowired
    QuestTypeScoreService questTypeScoreService;

    @Autowired
    MongoDatabase scoreDatabase;

    @Override
    protected void runTask(AggrTask aggrTask) {
        String projectId = aggrTask.getProjectId();
        Range range = aggrTask.getRange();

        MongoCollection<Document> srcCollection = scoreDatabase.getCollection("quest_type_score");
        MongoCollection<Document> dstCollection = scoreDatabase.getCollection("quest_type_score_average");

        Document _id = doc("questType", "$questType");
        Document avg = doc("$avg", "$score");

        srcCollection.aggregate(Arrays.asList(
                $match(doc("project", projectId).append(range.getName(), range.getId())),
                $group(doc("_id", _id).append("average", avg))
        )).forEach((Consumer<Document>) document -> {
            String questType = ((Document) document.get("_id")).getString("questType");
            double average = document.getDouble("average");

            Document query = doc("project", projectId)
                    .append("range", range2Doc(range))
                    .append("questType", target2Doc(questType(questType)));

            dstCollection.updateOne(query, $set("average", average), UPSERT);
        });
    }
}
