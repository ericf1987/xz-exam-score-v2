package com.xz.examscore.asynccomponents.aggrtask.impl;

import com.hyd.simplecache.utils.MD5;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.examscore.asynccomponents.aggrtask.AggrTask;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMessage;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMeta;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.AverageService;
import com.xz.examscore.services.ClassService;
import com.xz.examscore.services.SchoolService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;
import static com.xz.examscore.util.Mongo.range2Doc;
import static com.xz.examscore.util.Mongo.target2Doc;

/**
 * 计算超均率（考试项目总分）
 */
@AggrTaskMeta(taskType = "over_average")
@Component
public class OverAverageTask extends AggrTask {

    @Autowired
    ClassService classService;

    @Autowired
    SchoolService schoolService;

    @Autowired
    AverageService averageService;

    @Autowired
    MongoDatabase scoreDatabase;

    @Override
    protected void runTask(AggrTaskMessage taskInfo) {
        Range range = taskInfo.getRange();

        if (range.match(Range.CLASS)) {
            processClassOverAverage(taskInfo);
        } else if (range.match(Range.SCHOOL)) {
            processSchoolOverAverage(taskInfo);
        }
    }

    private void processClassOverAverage(AggrTaskMessage taskInfo) {
        Target target = taskInfo.getTarget();
        String classId = taskInfo.getRange().getId();
        String projectId = taskInfo.getProjectId();

        Document classDoc = classService.findClass(projectId, classId);
        String schoolId = classDoc.getString("school");

        double classAvg = averageService.getAverage(projectId, Range.clazz(classId), Target.project(projectId));
        double schoolAvg = averageService.getAverage(projectId, Range.school(schoolId), Target.project(projectId));
        double overAverage = (classAvg - schoolAvg) / schoolAvg;

        saveOverAverage(projectId, Range.clazz(classId), target, overAverage);
    }

    private void processSchoolOverAverage(AggrTaskMessage taskInfo) {
        Target target = taskInfo.getTarget();
        String schoolId = taskInfo.getRange().getId();
        String projectId = taskInfo.getProjectId();

        Document schoolDoc = schoolService.findSchool(projectId, schoolId);
        String province = schoolDoc.getString("province");

        double schoolAvg = averageService.getAverage(projectId, Range.school(schoolId), target);
        double provinceAvg = averageService.getAverage(projectId, Range.province(province), target);
        double overAverage = (schoolAvg - provinceAvg) / provinceAvg;

        saveOverAverage(projectId, Range.school(schoolId), target, overAverage);
    }

    private void saveOverAverage(String projectId, Range range, Target target, double overAverage) {
        MongoCollection<Document> collection = scoreDatabase.getCollection("over_average");

        Document query = doc("project", projectId)
                .append("range", range2Doc(range)).append("target", target2Doc(target));

        collection.deleteMany(query);
        collection.insertOne(doc(query).append("overAverage", overAverage).append("md5", MD5.digest(UUID.randomUUID().toString())));
    }

}
