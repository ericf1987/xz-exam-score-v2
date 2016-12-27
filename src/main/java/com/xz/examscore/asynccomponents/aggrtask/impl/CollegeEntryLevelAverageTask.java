package com.xz.examscore.asynccomponents.aggrtask.impl;

import com.hyd.simplecache.utils.MD5;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import com.xz.examscore.asynccomponents.aggrtask.AggrTask;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMessage;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMeta;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.*;
import com.xz.examscore.util.Mongo;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.xz.ajiaedu.common.mongo.MongoUtils.$set;
import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;

/**
 * @author by fengye on 2016/12/22.
 */
@Component
@AggrTaskMeta(taskType = "college_entry_level_average")
public class CollegeEntryLevelAverageTask extends AggrTask {

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    ProvinceService provinceService;

    @Autowired
    ProjectService projectService;

    @Autowired
    ScoreService scoreService;

    @Autowired
    CollegeEntryLevelService collegeEntryLevelService;

    @Autowired
    StudentService studentService;

    @Override
    protected void runTask(AggrTaskMessage taskInfo) {
        String projectId = taskInfo.getProjectId();
        Range range = taskInfo.getRange();
        Target target = taskInfo.getTarget();
        Range provinceRange = Range.province(provinceService.getProjectProvince(projectId));
        Target projectTarget = Target.project(projectId);
        MongoCollection<Document> collection = scoreDatabase.getCollection("college_entry_level_average");

        //查询本次考试的本科批次
        List<String> entryLevel = collegeEntryLevelService.getEntryLevelKey(projectId);
        List<Document> entryLevelDoc = collegeEntryLevelService.getEntryLevelDoc(projectId);

        double onlineTotalScore = 0;
        double onlineTotalCount = 0;

        for (String key : entryLevel){
            ArrayList<Document> entryLevelStudent = collegeEntryLevelService.getEntryLevelStudentByKey(projectId, provinceRange, projectTarget, key);
            //指定本科批次的人数
            int count = entryLevelStudent.size();
            double totalScore = 0;
            for (Document doc : entryLevelStudent){
                String studentId = doc.getString("student");
                double score = scoreService.getScore(projectId, Range.student(studentId), target);
                totalScore += score;
            }
            double average = count == 0 ? 0 : totalScore / count;
            Optional<Document> level = entryLevelDoc.stream().filter(doc -> doc.getString("level").equals(key)).findFirst();
            Document query = Mongo.query(projectId, range, target).append("college_entry_level", level.get());

            // 保存平均分
            UpdateResult result = collection.updateMany(query, $set(doc("average", average)));
            if(result.getMatchedCount() == 0){
                collection.insertOne(query.append("average", average).append("md5", MD5.digest(UUID.randomUUID().toString())));
            }

            //累加本科上线的总分
            onlineTotalScore += totalScore;
            //累加本科上线的人数
            onlineTotalCount += count;
        }

        saveOffLineAverage(projectId, range, target, collection, onlineTotalScore, onlineTotalCount);
    }

    private void saveOffLineAverage(String projectId, Range range, Target target, MongoCollection<Document> collection, double onlineTotalScore, double onlineTotalCount) {
        //查询当前维度和目标的总分
        double subCount = studentService.getStudentCount(projectId, range, target) - onlineTotalCount;
        double subTotal = scoreService.getScore(projectId, range, target) - onlineTotalScore;
        double offLineAverage = subCount == 0 ? 0 : subTotal / subCount;

        if(subCount < 0 || subTotal < 0) {
            throw new IllegalArgumentException("统计本科未上线学生平均分异常！");
        }

        // 保存平均分

        Document query = Mongo.query(projectId, range, target);
        query.append("college_entry_level", doc("score", 0).append("level", "OFFLINE"));

        UpdateResult result = collection.updateMany(query, $set(doc("average", offLineAverage)));
        if(result.getMatchedCount() == 0){
            collection.insertOne(query.append("average", offLineAverage).append("md5", MD5.digest(UUID.randomUUID().toString())));
        }

    }
}
