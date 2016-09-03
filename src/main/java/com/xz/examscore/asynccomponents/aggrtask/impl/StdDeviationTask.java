package com.xz.examscore.asynccomponents.aggrtask.impl;

import com.hyd.simplecache.utils.MD5;
import com.mongodb.client.MongoDatabase;
import com.xz.examscore.asynccomponents.aggrtask.AggrTask;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMessage;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMeta;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.AverageService;
import com.xz.examscore.services.ScoreService;
import com.xz.examscore.services.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static com.xz.ajiaedu.common.mongo.MongoUtils.$set;
import static com.xz.ajiaedu.common.mongo.MongoUtils.UPSERT;
import static com.xz.examscore.util.Mongo.query;

@AggrTaskMeta(taskType = "std_deviation")
@Component
public class StdDeviationTask extends AggrTask {

    @Autowired
    StudentService studentService;

    @Autowired
    ScoreService scoreService;

    @Autowired
    AverageService averageService;

    @Autowired
    MongoDatabase scoreDatabase;

    @Override
    protected void runTask(AggrTaskMessage taskInfo) {
        String projectId = taskInfo.getProjectId();
        Range range = taskInfo.getRange();
        Target target = taskInfo.getTarget();

        List<String> studentIds = studentService.getStudentIds(projectId, range, target);
        double average = averageService.getAverage(projectId, range, target);
        double delta = 0;

        for (String studentId : studentIds) {
            double score = scoreService.getScore(projectId, Range.student(studentId), target);
            delta += (score - average) * (score - average);
        }

        double deviation = Math.sqrt(delta / studentIds.size());
        scoreDatabase.getCollection("std_deviation").updateOne(
                query(projectId, range, target), $set("stdDeviation", deviation).append("md5", MD5.digest(UUID.randomUUID().toString()))
                , UPSERT);
    }
}
