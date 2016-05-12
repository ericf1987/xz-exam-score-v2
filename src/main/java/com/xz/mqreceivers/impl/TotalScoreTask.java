package com.xz.mqreceivers.impl;

import com.alibaba.fastjson.JSON;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import com.xz.bean.Range;
import com.xz.bean.SubjectObjective;
import com.xz.bean.Target;
import com.xz.mqreceivers.AggrTask;
import com.xz.mqreceivers.Receiver;
import com.xz.mqreceivers.ReceiverInfo;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.function.Consumer;

import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;

@Component
@ReceiverInfo(taskType = "total_score")
public class TotalScoreTask extends Receiver {

    @Autowired
    MongoDatabase scoreDatabase;

    @Override
    protected void runTask(AggrTask aggrTask) {
        String projectId = aggrTask.getProjectId();
        Target target = aggrTask.getTarget();
        Range range = aggrTask.getRange();

        Document match = new Document("project", projectId);
        match.append(target.getName(), target.getId());
        match.append(range.getName(), range.getId());

        if (target.getName().equals(Target.SUBJECT_OBJECTIVE)) {
            fixMatchObjective(match, target);
        }

        saveAggregate(projectId, match, aggrTask);
    }

    @SuppressWarnings("unchecked")
    private void fixMatchObjective(Document match, Target target) {
        SubjectObjective id = target.getId(SubjectObjective.class);
        match.remove(target.getName());
        match.append("subject", id.getSubject()).append("isObjective", id.isObjective());
    }

    private void saveAggregate(String projectId, Document match, AggrTask aggrTask) {
        Target target = aggrTask.getTarget();
        Range range = aggrTask.getRange();

        Document group = new Document()
                .append("_id", null)
                .append("totalScore", new Document("$sum", "$score"));

        MongoCollection<Document> scoreCollection = scoreDatabase.getCollection("score");
        AggregateIterable<Document> aggregate = scoreCollection.aggregate(Arrays.asList(
                new Document("$match", match),
                new Document("$group", group)
        ));

        Object targetId = target.getId() instanceof String ?
                target.getId() : Document.parse(JSON.toJSONString(target.getId()));

        Document saveKey = doc("project", projectId)
                .append("target", doc("name", target.getName()).append("id", targetId))
                .append("range", doc("name", range.getName()).append("id", range.getId()));

        MongoCollection<Document> totalScoreCollection = scoreDatabase.getCollection("total_score");
        aggregate.forEach((Consumer<Document>) document -> {
            Document totalScore = new Document(saveKey)
                    .append("totalScore", document.get("totalScore"))
                    .append("_id", new ObjectId());

            totalScoreCollection.replaceOne(saveKey, totalScore, new UpdateOptions().upsert(true));
        });
    }
}
