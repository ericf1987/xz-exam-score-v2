package com.xz.mqreceivers.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.mqreceivers.AggrTask;
import com.xz.mqreceivers.Receiver;
import com.xz.mqreceivers.ReceiverInfo;
import com.xz.services.ScoreService;
import com.xz.services.StudentService;
import com.xz.util.Mongo;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.xz.ajiaedu.common.mongo.MongoUtils.*;

/**
 * 生成排名记录
 */
@ReceiverInfo(taskType = "rank")
@Component
public class RankTask extends Receiver {

    @Autowired
    ScoreService scoreService;

    @Autowired
    StudentService studentService;

    @Autowired
    MongoDatabase scoreDatabase;

    @Override
    protected void runTask(AggrTask aggrTask) {
        String projectId = aggrTask.getProjectId();
        Range range = aggrTask.getRange();
        Target target = aggrTask.getTarget();

        List<String> studentIds = studentService.getStudentList(projectId, range, target);
        MongoCollection<Document> collection = scoreDatabase.getCollection("rank_map");
        Document id = Mongo.generateId(projectId, range, target);

        collection.deleteOne(doc("_id", id));

        for (String studentId : studentIds) {
            Range studentRange = new Range(Range.STUDENT, studentId);
            double totalScore = scoreService.getScore(projectId, studentRange, target);

            // 先尝试 update，如果受影响的记录数为 0 则表示记录不存在，再做 insert
            UpdateResult updateResult = collection.updateOne(
                    doc("_id", id).append("scoreMap", $elemMatch("score", totalScore)),
                    doc("$inc", doc("scoreMap.$.count", 1))
            );

            if (updateResult.getModifiedCount() == 0) {
                collection.updateOne(
                        doc("_id", id).append("scoreMap.score", $ne(totalScore)),
                        $push("scoreMap", doc("score", totalScore).append("count", 1)),
                        new UpdateOptions().upsert(true)
                );
            }

        }
    }
}
