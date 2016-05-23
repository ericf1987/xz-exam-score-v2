package com.xz.mqreceivers.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.bean.SubjectObjective;
import com.xz.bean.Target;
import com.xz.mqreceivers.AggrTask;
import com.xz.mqreceivers.Receiver;
import com.xz.mqreceivers.ReceiverInfo;
import com.xz.services.QuestService;
import com.xz.services.TargetService;
import com.xz.util.Mongo;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static com.xz.ajiaedu.common.mongo.MongoUtils.*;

@Component
@ReceiverInfo(taskType = "full_score")
public class FullScoreTask extends Receiver {

    @Autowired
    QuestService questService;

    @Autowired
    TargetService targetService;

    @Autowired
    MongoDatabase scoreDatabase;

    @Override
    protected void runTask(AggrTask aggrTask) {
        Target target = aggrTask.getTarget();

        if (target.match(Target.QUEST_TYPE)) {
            processQuestType(aggrTask);
        } else if (target.match(Target.SUBJECT_OBJECTIVE)) {
            processSubjectObjective(aggrTask);
        } else if (target.match(Target.POINT)) {
            processPoint(aggrTask);
        } else if (target.match(Target.ABILITY_LEVEL)) {
            processAbilityLevel(aggrTask);
        }
    }

    private void processAbilityLevel(AggrTask aggrTask) {

    }

    private void processPoint(AggrTask aggrTask) {

    }

    private void processSubjectObjective(AggrTask aggrTask) {
        String projectId = aggrTask.getProjectId();
        Target target = aggrTask.getTarget();
        SubjectObjective subjectObjective = target.getId(SubjectObjective.class);
        double fullScore = 0;

        // 查询满分
        Document aggrResult = scoreDatabase.getCollection("quest_list").aggregate(Arrays.asList(
                $match(doc("project", projectId)
                        .append("subject", subjectObjective.getSubject())
                        .append("isObjective", subjectObjective.isObjective())),

                $group(doc("_id", null).append("fullScore", $sum("$score")))
        )).first();

        if (aggrResult != null) {
            fullScore = aggrResult.getDouble("fullScore");
        }

        // 保存满分
        Document query = doc("project", projectId).append("target", Mongo.target2Doc(target));
        MongoCollection<Document> fullScoreCollection = scoreDatabase.getCollection("full_score");
        fullScoreCollection.deleteMany(query);
        fullScoreCollection.insertOne(doc(query).append("fullScore", fullScore));
    }

    private void processQuestType(AggrTask aggrTask) {

    }
}
