package com.xz.examscore.asynccomponents.aggrtask.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.examscore.asynccomponents.aggrtask.AggrTask;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMessage;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMeta;
import com.xz.examscore.bean.SubjectObjective;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.QuestService;
import com.xz.examscore.services.TargetService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.function.Consumer;

import static com.xz.ajiaedu.common.mongo.MongoUtils.*;
import static com.xz.examscore.util.Mongo.target2Doc;

@Component
@AggrTaskMeta(taskType = "full_score")
public class FullScoreTask extends AggrTask {

    @Autowired
    QuestService questService;

    @Autowired
    TargetService targetService;

    @Autowired
    MongoDatabase scoreDatabase;

    @Override
    protected void runTask(AggrTaskMessage taskInfo) {
        Target target = taskInfo.getTarget();

        // 知识点/能力层级的满分统计在 PointTask 中完成
        if (target.match(Target.QUEST_TYPE)) {
            processQuestType(taskInfo);
        } else if (target.match(Target.SUBJECT_OBJECTIVE)) {
            processSubjectObjective(taskInfo);
        }
    }

    // 计算科目主客观题满分
    private void processSubjectObjective(AggrTaskMessage taskInfo) {
        String projectId = taskInfo.getProjectId();
        Target target = taskInfo.getTarget();
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
        Document query = doc("project", projectId).append("target", target2Doc(target));
        MongoCollection<Document> fullScoreCollection = scoreDatabase.getCollection("full_score");
        fullScoreCollection.deleteMany(query);
        fullScoreCollection.insertOne(doc(query).append("fullScore", fullScore));
    }

    // 计算题型满分
    private void processQuestType(AggrTaskMessage taskInfo) {
        String projectId = taskInfo.getProjectId();

        MongoCollection<Document> questList = scoreDatabase.getCollection("quest_list");
        MongoCollection<Document> fullScores = scoreDatabase.getCollection("full_score");

        questList.aggregate(Arrays.asList(
                $match(doc("project", projectId).append("questionTypeId", $ne(null))),
                $group(doc("_id", doc("questType", "$questionTypeId"))
                        .append("fullScore", doc("$sum", "$score")))

        )).forEach((Consumer<Document>) document -> {
            String questType = ((Document) document.get("_id")).getString("questType");
            double fullScore = document.getDouble("fullScore");

            fullScores.updateOne(
                    doc("project", projectId).append("target", target2Doc(Target.questType(questType))),
                    $set("fullScore", fullScore), UPSERT);
        });

    }
}
