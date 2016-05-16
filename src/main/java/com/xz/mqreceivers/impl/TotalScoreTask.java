package com.xz.mqreceivers.impl;

import com.alibaba.fastjson.JSON;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.bean.Range;
import com.xz.bean.SubjectObjective;
import com.xz.bean.Target;
import com.xz.mqreceivers.AggrTask;
import com.xz.mqreceivers.Receiver;
import com.xz.mqreceivers.ReceiverInfo;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

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

        Object totalScoreValue = getTotalScoreValue(match);
        Range range = aggrTask.getRange();
        Target target = aggrTask.getTarget();

        Document saveKey = doc("project", projectId)
                .append("target", doc("name", target.getName()).append("id", target.idToParam()))
                .append("range", doc("name", range.getName()).append("id", range.getId()));

        MongoCollection<Document> totalScoreCollection = scoreDatabase.getCollection("total_score");
        Document totalScore = new Document(saveKey)
                .append("totalScore", totalScoreValue);

        totalScoreCollection.deleteMany(saveKey);
        totalScoreCollection.insertOne(totalScore);
    }

    private Object getTotalScoreValue(Document match) {
        Document group = new Document()
                .append("_id", null)
                .append("totalScore", new Document("$sum", "$score"));

        AggregateIterable<Document> aggregate = scoreDatabase.getCollection("score")
                .aggregate(Arrays.asList(
                        new Document("$match", match),
                        new Document("$group", group)
                ));
        Document aggregateResult = aggregate.first();
        return aggregateResult.get("totalScore");
    }

}
