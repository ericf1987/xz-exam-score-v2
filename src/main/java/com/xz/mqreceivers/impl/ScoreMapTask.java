package com.xz.mqreceivers.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
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

import java.util.ArrayList;
import java.util.List;

import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;

/**
 * 生成排名记录
 */
@ReceiverInfo(taskType = "score_map")
@Component
public class ScoreMapTask extends Receiver {

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
        MongoCollection<Document> collection = scoreDatabase.getCollection("score_map");
        Document query = doc("project", projectId)
                .append("range", Mongo.range2Doc(range))
                .append("target", Mongo.target2Doc(target));

        // 删除旧记录
        collection.deleteMany(query);

        // 保存新记录
        List<Document> scoreCountList = createScoreMap(projectId, target, studentIds);
        collection.insertOne(doc(query).append("scoreMap", scoreCountList).append("count", studentIds.size()));
    }

    private List<Document> createScoreMap(String projectId, Target target, List<String> studentIds) {
        List<Document> scoreCountList = new ArrayList<>();

        for (String studentId : studentIds) {
            Range studentRange = new Range(Range.STUDENT, studentId);
            double totalScore = scoreService.getScore(projectId, studentRange, target);
            addUpScoreMap(scoreCountList, totalScore);
        }

        return scoreCountList;
    }

    private void addUpScoreMap(List<Document> scoreCountList, double score) {
        for (Document document : scoreCountList) {
            if (document.getDouble("score") == score) {
                document.put("count", document.getInteger("count") + 1);
                return;
            }
        }

        scoreCountList.add(doc("score", score).append("count", 1));
    }
}
