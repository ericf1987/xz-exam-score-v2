package com.xz.mqreceivers.impl;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.xz.mqreceivers.AggrTask;
import com.xz.mqreceivers.Receiver;
import com.xz.mqreceivers.ReceiverInfo;
import com.xz.services.QuestService;
import com.xz.services.ScoreService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ReceiverInfo(taskType = "student_quest_type_score")
public class StudentQuestTypeTask extends Receiver {

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    ScoreService scoreService;

    @Autowired
    QuestService questService;

    @Override
    protected void runTask(AggrTask aggrTask) {
        String projectId = aggrTask.getProjectId();
        String studentId = aggrTask.getRange().getId();

        FindIterable<Document> scores = scoreService.getStudentQuestScores(projectId, studentId);
        Map<String, Double> questionTypeScores = new HashMap<>();

        for (Document score : scores) {
            String questNo = score.getString("questNo");
            String subject = score.getString("subject");

            Document quest = questService.findQuest(projectId, subject, questNo);
            String questionTypeId = quest.getString("questionTypeId");

        }
    }
}
