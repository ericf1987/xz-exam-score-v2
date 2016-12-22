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
import com.xz.examscore.services.CollegeEntryLevelService;
import com.xz.examscore.services.ProjectService;
import com.xz.examscore.services.ProvinceService;
import com.xz.examscore.services.ScoreService;
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
        for (String key : entryLevel){
            ArrayList<Document> entryLevelStudent = collegeEntryLevelService.getEntryLevelStudentByKey(projectId, provinceRange, projectTarget, key);
            //指定本科批次的人数
            int count = entryLevelStudent.size();
            System.out.println("批次为：" + key + ", 学生人数：" + count);
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
        }
    }
}
