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

@Component
@ReceiverInfo(taskType = "nonstu_quest_type_score")
public class NonStuQuestTypeTask extends Receiver {

    @Autowired
    QuestTypeScoreService questTypeScoreService;

    @Autowired
    MongoDatabase scoreDatabase;

    @Override
    protected void runTask(AggrTask aggrTask) {
        String projectId = aggrTask.getProjectId();
        Range range = aggrTask.getRange();

        MongoCollection<Document> collection = scoreDatabase.getCollection("quest_type_score");

    }
}
