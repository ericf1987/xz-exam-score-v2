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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.xz.ajiaedu.common.mongo.MongoUtils.$set;
import static com.xz.ajiaedu.common.mongo.MongoUtils.UPSERT;
import static com.xz.util.Mongo.query;

@ReceiverInfo(taskType = "std_deviation")
@Component
public class StdDeviationTask extends Receiver {

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

        List<String> studentIds = studentService.getStudentList(projectId, range, target);
        double average = averageService.getAverage(projectId, range, target);
        double delta = 0;

        for (String studentId : studentIds) {
            double score = scoreService.getScore(projectId, Range.student(studentId), target);
            delta += (score - average) * (score - average);
        }

        double deviation = Math.sqrt(delta / studentIds.size());
        scoreDatabase.getCollection("std_deviation").updateOne(
                query(projectId, range, target), $set("stdDeviation", deviation), UPSERT);
    }
}
