package com.xz.mqreceivers.impl;

import com.mongodb.client.MongoDatabase;
import com.xz.ajiaedu.common.mongo.MongoUtils;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.mqreceivers.AggrTask;
import com.xz.mqreceivers.Receiver;
import com.xz.mqreceivers.ReceiverInfo;
import com.xz.services.AverageService;
import com.xz.services.ScoreService;
import com.xz.services.TargetService;
import com.xz.util.Mongo;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ReceiverInfo(taskType = "subject_rate")
@Component
public class SubjectRateTask extends Receiver {

    @Autowired
    AverageService averageService;

    @Autowired
    TargetService targetService;

    @Autowired
    ScoreService scoreService;

    @Autowired
    MongoDatabase scoreDatabase;

    @Override
    protected void runTask(AggrTask aggrTask) {
        Range range = aggrTask.getRange();

        if (range.match(Range.STUDENT)) {
            processStudentSubjectRate(aggrTask);
        } else {
            processNonStudentSubjectRate(aggrTask);
        }
    }

    private void processStudentSubjectRate(AggrTask aggrTask) {
        String projectId = aggrTask.getProjectId();
        Range range = aggrTask.getRange();
        Target target = aggrTask.getTarget();

        Map<String, Double> subjectAverages = scoreService.getAllSubjectScore(projectId, range);
        double totalAverage = subjectAverages.values().stream().mapToDouble(d -> d).sum();
        List<Document> subjectRates = new ArrayList<>();

        for (String subjectId : subjectAverages.keySet()) {
            Double avg = subjectAverages.get(subjectId);
            subjectRates.add(MongoUtils.doc("subject", subjectId).append("rate", avg / totalAverage));
        }

        scoreDatabase.getCollection("subject_rate").updateOne(
                Mongo.query(projectId, range, target),
                MongoUtils.$set("subjectRates", subjectRates)
        );
    }

    private void processNonStudentSubjectRate(AggrTask aggrTask) {
        String projectId = aggrTask.getProjectId();
        Range range = aggrTask.getRange();
        Target target = aggrTask.getTarget();

        Map<String, Double> subjectAverages = averageService.getAllSubjectAverage(projectId, range);
        double totalAverage = subjectAverages.values().stream().mapToDouble(d -> d).sum();
        List<Document> subjectRates = new ArrayList<>();

        for (String subjectId : subjectAverages.keySet()) {
            Double avg = subjectAverages.get(subjectId);
            subjectRates.add(MongoUtils.doc("subject", subjectId).append("rate", avg / totalAverage));
        }

        scoreDatabase.getCollection("subject_rate").updateOne(
                Mongo.query(projectId, range, target),
                MongoUtils.$set("subjectRates", subjectRates)
        );
    }
}
