package com.xz.mqreceivers.impl;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.bean.Target;
import com.xz.mqreceivers.AggrTask;
import com.xz.mqreceivers.Receiver;
import com.xz.mqreceivers.ReceiverInfo;
import com.xz.services.FullScoreService;
import com.xz.services.QuestService;
import com.xz.services.ScoreService;
import com.xz.services.StudentService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static com.xz.ajiaedu.common.mongo.MongoUtils.*;

@Component
@ReceiverInfo(taskType = "quest_type_score")
public class QuestTypeScoreTask extends Receiver {

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    ScoreService scoreService;

    @Autowired
    FullScoreService fullScoreService;

    @Autowired
    QuestService questService;

    @Autowired
    StudentService studentService;

    @Override
    protected void runTask(AggrTask aggrTask) {
        String projectId = aggrTask.getProjectId();
        String studentId = aggrTask.getRange().getId();

        // 查询考生所有题目成绩
        Document studentDoc = studentService.findStudent(projectId, studentId);
        FindIterable<Document> scores = scoreService.getStudentQuestScores(projectId, studentId);
        Map<String, Double> questTypeScores = new HashMap<>();

        // 对题型进行归类，分数累加
        for (Document score : scores) {
            String questNo = score.getString("questNo");
            String subject = score.getString("subject");
            double scoreValue = score.getDouble("score");

            Document quest = questService.findQuest(projectId, subject, questNo);
            String questTypeId = quest.getString("questionTypeId");

            // 有的科目可能因为没有录入题目而导致该属性为空。
            if (questTypeId == null) {
                continue;
            }

            if (!questTypeScores.containsKey(questTypeId)) {
                questTypeScores.put(questTypeId, scoreValue);
            } else {
                questTypeScores.put(questTypeId, questTypeScores.get(questTypeId) + scoreValue);
            }
        }

        // 保存考生题型得分
        MongoCollection<Document> collection = scoreDatabase.getCollection("quest_type_score");

        for (String questTypeId : questTypeScores.keySet()) {
            Document query = doc("project", projectId).append("student", studentId).append("questType", questTypeId);
            double score = questTypeScores.get(questTypeId);
            double fullScore = fullScoreService.getFullScore(projectId, Target.questType(questTypeId));
            double rate = score / fullScore;

            Document update = doc("score", score)
                    .append("rate", rate)
                    .append("class", studentDoc.getString("class"))
                    .append("school", studentDoc.getString("school"))
                    .append("area", studentDoc.getString("area"))
                    .append("city", studentDoc.getString("city"))
                    .append("province", studentDoc.getString("province"));

            collection.updateOne(query, $set(update), UPSERT);
        }
    }
}
