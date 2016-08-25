package com.xz.examscore.asynccomponents.aggrtask.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.examscore.asynccomponents.aggrtask.AggrTask;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskInfo;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMeta;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.FullScoreService;
import com.xz.examscore.services.QuestTypeScoreService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.function.Consumer;

import static com.xz.ajiaedu.common.mongo.MongoUtils.*;
import static com.xz.examscore.util.Mongo.range2Doc;

@Component
@AggrTaskMeta(taskType = "quest_type_score_average")
public class QuestTypeScoreAverageTask extends AggrTask {

    @Autowired
    QuestTypeScoreService questTypeScoreService;

    @Autowired
    FullScoreService fullScoreService;

    @Autowired
    MongoDatabase scoreDatabase;

    @Override
    protected void runTask(AggrTaskInfo taskInfo) {
        String projectId = taskInfo.getProjectId();
        Range range = taskInfo.getRange();

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
            double fullScore = fullScoreService.getFullScore(projectId, Target.questType(questType));
            double rate = average / fullScore;

            Document query = doc("project", projectId)
                    .append("range", range2Doc(range))
                    .append("questType", questType);

            Document update = doc("average", average).append("rate", rate);

            dstCollection.updateOne(query, $set(update), UPSERT);
        });
    }
}
