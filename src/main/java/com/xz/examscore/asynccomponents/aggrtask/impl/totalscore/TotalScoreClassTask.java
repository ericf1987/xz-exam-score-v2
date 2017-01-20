package com.xz.examscore.asynccomponents.aggrtask.impl.totalscore;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.examscore.asynccomponents.aggrtask.AggrTask;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMessage;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMeta;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.RangeService;
import com.xz.examscore.services.ScoreService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static com.xz.ajiaedu.common.mongo.MongoUtils.*;
import static com.xz.examscore.util.Mongo.target2Doc;

@Component
@AggrTaskMeta(taskType = "total_score_class")
public class TotalScoreClassTask extends AggrTask {

    @Autowired
    RangeService rangeService;

    @Autowired
    private MongoDatabase scoreDatabase;

    @Autowired
    ScoreService scoreService;

    @Override
    protected void runTask(AggrTaskMessage taskInfo) {
        Target target = taskInfo.getTarget();

        if (target.match(Target.QUEST)) {
            aggrQuestCombinationScores(taskInfo);
        } else {
            aggrNonQuestCombinationScores(taskInfo, target);
        }
    }

    private void aggrQuestCombinationScores(AggrTaskMessage taskInfo) {
        MongoCollection<Document> scoreCollection = scoreDatabase.getCollection("score");

        AggregateIterable<Document> aggregate = scoreCollection.aggregate(Arrays.asList(
                $match(doc("project", taskInfo.getProjectId())),
                $group(doc(
                        "_id", doc("class", "$class").append("quest", "$quest")
                ).append(
                        "totalScore", doc("$sum", "$score")
                ))
        ));

        for (Document doc : aggregate) {
            String classId = doc.get("_id", Document.class).getString("class");
            String questId = doc.get("_id", Document.class).getString("quest");
            double totalScore = doc.getDouble("totalScore");
            scoreService.saveTotalScore(taskInfo.getProjectId(), Range.clazz(classId), Target.quest(questId), totalScore);
        }
    }

    private void aggrNonQuestCombinationScores(AggrTaskMessage taskInfo, Target target) {
        MongoCollection<Document> totalScoreCollection = scoreDatabase.getCollection("total_score");

        AggregateIterable<Document> aggregate = totalScoreCollection.aggregate(Arrays.asList(
                $match(doc("project", taskInfo.getProjectId()).append("range.name", "student").append("target", target2Doc(target))),
                $group(doc("_id", "$class").append("totalScore", doc("$sum", "$totalScore")))
        ));

        for (Document doc : aggregate) {
            String rangeId = doc.getString("_id");
            double totalScore = doc.getDouble("totalScore");
            scoreService.saveTotalScore(taskInfo.getProjectId(), Range.clazz(rangeId), target, totalScore);
        }
    }
}
