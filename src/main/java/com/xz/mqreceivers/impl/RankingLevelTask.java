package com.xz.mqreceivers.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.mqreceivers.AggrTask;
import com.xz.mqreceivers.Receiver;
import com.xz.mqreceivers.ReceiverInfo;
import com.xz.services.RankService;
import com.xz.services.StudentService;
import com.xz.util.Mongo;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.xz.ajiaedu.common.mongo.MongoUtils.$set;
import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;

@ReceiverInfo(taskType = "ranking_level")
@Component
public class RankingLevelTask extends Receiver {

    @Autowired
    RankService rankService;

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    StudentService studentService;

    @Override
    protected void runTask(AggrTask aggrTask) {
        String projectId = aggrTask.getProjectId();
        Range rankRange = aggrTask.getRange();
        Target target = aggrTask.getTarget();

        MongoCollection<Document> totalScoreCollection = scoreDatabase.getCollection("total_score");
        List<String> studentList = studentService.getStudentList(projectId, rankRange, target);

        for (String studentId : studentList) {
            String rankLevel = rankService.getRankLevel(projectId, rankRange, target, studentId);

            totalScoreCollection.updateOne(
                    doc("_id", Mongo.generateId(projectId, Range.student(studentId), target)),
                    $set("rankLevel." + rankRange.getName(), rankLevel));
        }
    }
}
