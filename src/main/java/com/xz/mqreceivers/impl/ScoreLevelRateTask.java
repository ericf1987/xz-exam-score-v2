package com.xz.mqreceivers.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.mqreceivers.AggrTask;
import com.xz.mqreceivers.Receiver;
import com.xz.mqreceivers.ReceiverInfo;
import com.xz.services.ScoreLevelService;
import com.xz.services.StudentService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;
import static com.xz.util.Mongo.range2Doc;
import static com.xz.util.Mongo.target2Doc;

@Component
@ReceiverInfo(taskType = "score_level_rate")
public class ScoreLevelRateTask extends Receiver {

    @Autowired
    StudentService studentService;

    @Autowired
    ScoreLevelService scoreLevelService;

    @Autowired
    MongoDatabase scoreDatabase;

    @Override
    protected void runTask(AggrTask aggrTask) {
        String projectId = aggrTask.getProjectId();
        Range range = aggrTask.getRange();
        Target target = aggrTask.getTarget();

        List<String> studentList = studentService.getStudentList(projectId, range, target);
        Map<String, AtomicInteger> counters = new HashMap<>();

        // 计算各得分等级人数
        for (String studentId : studentList) {
            String scoreLevel = scoreLevelService.getScoreLevel(projectId, studentId, target);
            if (!counters.containsKey(scoreLevel)) {
                counters.put(scoreLevel, new AtomicInteger());
            }
            counters.get(scoreLevel).incrementAndGet();
        }

        // 计算各得分等级占比
        Map<String, Double> scoreLevelRate = new HashMap<>();
        for (String scoreLevel : counters.keySet()) {
            double rate = (double) (counters.get(scoreLevel).get()) / studentList.size();
            scoreLevelRate.put(scoreLevel, rate);
        }

        // 保存 scoreLevelRate
        MongoCollection<Document> collection = scoreDatabase.getCollection("score_level_rate");
        Document query = doc("project", projectId).append("range", range2Doc(range)).append("target", target2Doc(target));
        collection.deleteMany(query);
        collection.insertOne(doc(query).append("scoreLevelRate", scoreLevelRate));
    }
}
