package com.xz.mqreceivers.impl;

import com.mongodb.client.MongoDatabase;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.mqreceivers.AggrTask;
import com.xz.mqreceivers.Receiver;
import com.xz.mqreceivers.ReceiverInfo;
import com.xz.services.AverageService;
import com.xz.services.ScoreService;
import com.xz.services.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.xz.ajiaedu.common.mongo.MongoUtils.$set;
import static com.xz.util.Mongo.UPSERT;
import static com.xz.util.Mongo.generateId;

@ReceiverInfo(taskType = "std_deviation")
@Component
public class StdDeviationTask extends Receiver {

    static final Logger LOG = LoggerFactory.getLogger(StdDeviationTask.class);

    @Autowired
    StudentService studentService;

    @Autowired
    ScoreService scoreService;

    @Autowired
    AverageService averageService;

    @Autowired
    MongoDatabase scoreDatabase;

    @Override
    protected void runTask(AggrTask aggrTask) {
        String projectId = aggrTask.getProjectId();
        Range range = aggrTask.getRange();
        Target target = aggrTask.getTarget();

        Map<String, Double> studentScores = scoreService.getScores(projectId, range, target);

        List<String> studentIds = studentService.getStudentList(projectId, range, target);
        double average = averageService.getAverage(projectId, range, target);
        double delta = 0;

        for (String studentId : studentIds) {
            double score = scoreService.getScore(projectId, Range.student(studentId), target);
            delta += (score - average) * (score - average);
        }

        double deviation = Math.sqrt(delta / studentIds.size());
        scoreDatabase.getCollection("std_deviation").updateOne(
                generateId(projectId, range, target), $set("stdDeviation", deviation), UPSERT);
    }
}
