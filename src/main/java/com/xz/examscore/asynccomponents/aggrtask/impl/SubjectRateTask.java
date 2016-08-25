package com.xz.examscore.asynccomponents.aggrtask.impl;

import com.mongodb.client.MongoDatabase;
import com.xz.ajiaedu.common.mongo.MongoUtils;
import com.xz.examscore.asynccomponents.aggrtask.AggrTask;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMessage;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMeta;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.AverageService;
import com.xz.examscore.services.ScoreService;
import com.xz.examscore.services.TargetService;
import com.xz.examscore.util.Mongo;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AggrTaskMeta(taskType = "subject_rate")
@Component
public class SubjectRateTask extends AggrTask {

    @Autowired
    AverageService averageService;

    @Autowired
    TargetService targetService;

    @Autowired
    ScoreService scoreService;

    @Autowired
    MongoDatabase scoreDatabase;

    @Override
    protected void runTask(AggrTaskMessage taskInfo) {
        Range range = taskInfo.getRange();

        if (range.match(Range.STUDENT)) {
            processStudentSubjectRate(taskInfo);
        } else {
            processNonStudentSubjectRate(taskInfo);
        }
    }

    private void processStudentSubjectRate(AggrTaskMessage taskInfo) {
        String projectId = taskInfo.getProjectId();
        Range range = taskInfo.getRange();
        Target target = taskInfo.getTarget();

        Map<String, Double> subjectAverages = scoreService.getAllSubjectScore(projectId, range);
        double totalAverage = subjectAverages.values().stream().mapToDouble(d -> d).sum();
        List<Document> subjectRates = new ArrayList<>();

        for (String subjectId : subjectAverages.keySet()) {
            Double avg = subjectAverages.get(subjectId);
            subjectRates.add(MongoUtils.doc("subject", subjectId).append("rate", avg / totalAverage));
        }

        scoreDatabase.getCollection("subject_rate").updateOne(
                Mongo.query(projectId, range, target),
                MongoUtils.$set("subjectRates", subjectRates),
                MongoUtils.UPSERT
        );
    }

    private void processNonStudentSubjectRate(AggrTaskMessage taskInfo) {
        String projectId = taskInfo.getProjectId();
        Range range = taskInfo.getRange();
        Target target = taskInfo.getTarget();

        Map<String, Double> subjectAverages = averageService.getAllSubjectAverage(projectId, range);
        double totalAverage = subjectAverages.values().stream().mapToDouble(d -> d).sum();
        List<Document> subjectRates = new ArrayList<>();

        for (String subjectId : subjectAverages.keySet()) {
            Double avg = subjectAverages.get(subjectId);
            subjectRates.add(MongoUtils.doc("subject", subjectId).append("rate", avg / totalAverage));
        }

        scoreDatabase.getCollection("subject_rate").updateOne(
                Mongo.query(projectId, range, target),
                MongoUtils.$set("subjectRates", subjectRates),
                MongoUtils.UPSERT
        );
    }
}
