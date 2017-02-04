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
    private RangeService rangeService;

    @Autowired
    private MongoDatabase scoreDatabase;

    @Autowired
    private ScoreService scoreService;

    @Override
    protected void runTask(AggrTaskMessage taskInfo) {
        Target target = taskInfo.getTarget();

        // 考生题目分数放在 score 中，其他分数放在 total_score 中
        if (target.match(Target.QUEST)) {
            aggrFromScore(taskInfo);
        } else {
            aggrFromTotalScore(taskInfo);
        }
    }

    private void aggrFromScore(AggrTaskMessage taskInfo) {
        MongoCollection<Document> scoreCollection = scoreDatabase.getCollection("score");

        AggregateIterable<Document> aggregate = scoreCollection.aggregate(Arrays.asList(
                $match(doc("project", taskInfo.getProjectId()).append("quest", taskInfo.getTarget().getId())),
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
            Range resultRange = Range.clazz(classId);
            Target resultTarget = Target.quest(questId);

            Document parentsDocument = rangeService.getParentsDocument(taskInfo.getProjectId(), resultRange);
            scoreService.saveTotalScore(taskInfo.getProjectId(), resultRange, resultTarget, totalScore, parentsDocument);
        }
    }

    private void aggrFromTotalScore(AggrTaskMessage taskInfo) {
        aggrFromTotalScore(
                taskInfo, scoreDatabase, scoreService, rangeService, Range.STUDENT, Range.CLASS);
    }

    /**
     * 将下一级范围的总分加起来得出上一级范围的总分
     *
     * @param taskInfo      任务相关
     * @param scoreDatabase 分数数据库，用来执行 aggregation
     * @param scoreService  分数服务，用来保存计算结果
     * @param subRange      下一级范围名称
     * @param superRange    上一级范围名称
     */
    static void aggrFromTotalScore(
            AggrTaskMessage taskInfo,
            MongoDatabase scoreDatabase, ScoreService scoreService, RangeService rangeService,
            String subRange, String superRange) {

        Target target = taskInfo.getTarget();
        String projectId = taskInfo.getProjectId();
        MongoCollection<Document> totalScoreCollection = scoreDatabase.getCollection("total_score");

        AggregateIterable<Document> aggregate = totalScoreCollection.aggregate(Arrays.asList(
                $match(doc("project", projectId).append("range.name", subRange).append("target", target2Doc(target))),
                $group(doc("_id", "$" + superRange).append("totalScore", doc("$sum", "$totalScore")))
        ));

        for (Document doc : aggregate) {
            String rangeId = doc.getString("_id");

            Range resultRange = new Range();
            resultRange.setName(superRange);
            resultRange.setId(rangeId);

            double totalScore = doc.getDouble("totalScore");

            Document parentsDocument = rangeService.getParentsDocument(taskInfo.getProjectId(), resultRange);
            scoreService.saveTotalScore(projectId, resultRange, target, totalScore, parentsDocument);
        }
    }
}
