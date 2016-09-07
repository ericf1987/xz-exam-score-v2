package com.xz.examscore.asynccomponents.aggrtask.impl;

import com.hyd.simplecache.utils.MD5;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import com.xz.examscore.asynccomponents.aggrtask.AggrTask;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMessage;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMeta;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.AverageService;
import com.xz.examscore.services.ScoreService;
import com.xz.examscore.services.StudentService;
import com.xz.examscore.services.TargetService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.function.Consumer;

import static com.xz.ajiaedu.common.mongo.MongoUtils.$set;
import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;
import static com.xz.examscore.util.Mongo.range2Doc;

/**
 * 执行平均分统计
 */
@Component
@AggrTaskMeta(taskType = "average")
public class AverageTask extends AggrTask {

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    StudentService studentService;

    @Autowired
    AverageService averageService;

    @Autowired
    ScoreService scoreService;

    @Autowired
    TargetService targetService;

    @Override
    public void runTask(AggrTaskMessage taskInfo) {
        String projectId = taskInfo.getProjectId();
        Range range = taskInfo.getRange();

        // 根据 aggrTask 来遍历 total_score 集合中的记录，对每条记录计算平均分，再写回记录当中
        MongoCollection<Document> totalScoreCollection = scoreDatabase.getCollection("total_score");
        MongoCollection<Document> averageCollection = scoreDatabase.getCollection("average");

        FindIterable<Document> totalScores = totalScoreCollection.find(
                new Document("project", projectId).append("range", range2Doc(range)));

        totalScores.forEach((Consumer<Document>) document -> {
            Document rangeDoc = (Document) document.get("range");
            Document targetDoc = (Document) document.get("target");

            // 一定要从 scoreService.getScore() 方法获取
            double totalScore = scoreService.getScore(projectId, Range.parse(rangeDoc), Target.parse(targetDoc));

            Document query = doc();
            double average = calculateAverage(projectId, rangeDoc, targetDoc, totalScore, query);

            // 保存平均分
            UpdateResult result = averageCollection.updateMany(query, $set(doc("average", average)));
            if(result.getMatchedCount() == 0){
                averageCollection.insertOne(query.append("average", average).append("md5", MD5.digest(UUID.randomUUID().toString())));
            }
            averageService.deleteCache(projectId, range, Target.parse(targetDoc));
        });
    }

    protected double calculateAverage(
            String projectId, Document rangeDoc, Document targetDoc, double totalScore, Document query) {

        query.append("range", rangeDoc)
                .append("target", targetDoc)
                .append("project", projectId);

        String subjectId = targetService.getTargetSubjectId(projectId, Target.parse(targetDoc));

        // 计算平均分
        int studentCount = studentService.getStudentCount(projectId, subjectId, Range.parse(rangeDoc));
        double average;

        if (studentCount == 0) {
            average = 0;
        } else {
            average = totalScore / studentCount;
        }

        return average;
    }
}
