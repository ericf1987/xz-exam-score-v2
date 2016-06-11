package com.xz.mqreceivers.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.mqreceivers.AggrTask;
import com.xz.mqreceivers.Receiver;
import com.xz.mqreceivers.ReceiverInfo;
import com.xz.services.AverageService;
import com.xz.services.ClassService;
import com.xz.services.SchoolService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;
import static com.xz.util.Mongo.range2Doc;
import static com.xz.util.Mongo.target2Doc;

/**
 * 计算超均率（考试项目总分）
 */
@ReceiverInfo(taskType = "over_average")
@Component
public class OverAverageTask extends Receiver {

    @Autowired
    ClassService classService;

    @Autowired
    SchoolService schoolService;

    @Autowired
    AverageService averageService;

    @Autowired
    MongoDatabase scoreDatabase;

    @Override
    protected void runTask(AggrTask aggrTask) {
        Range range = aggrTask.getRange();

        if (range.match(Range.CLASS)) {
            processClassOverAverage(aggrTask);
        } else if (range.match(Range.SCHOOL)) {
            processSchoolOverAverage(aggrTask);
        }
    }

    private void processClassOverAverage(AggrTask aggrTask) {
        Target target = aggrTask.getTarget();
        String classId = aggrTask.getRange().getId();
        String projectId = aggrTask.getProjectId();

        Document classDoc = classService.findClass(projectId, classId);
        String schoolId = classDoc.getString("school");

        double classAvg = averageService.getAverage(projectId, Range.clazz(classId), Target.project(projectId));
        double schoolAvg = averageService.getAverage(projectId, Range.school(schoolId), Target.project(projectId));
        double overAverage = (classAvg - schoolAvg) / schoolAvg;

        saveOverAverage(projectId, Range.clazz(classId), target, overAverage);
    }

    private void processSchoolOverAverage(AggrTask aggrTask) {
        Target target = aggrTask.getTarget();
        String schoolId = aggrTask.getRange().getId();
        String projectId = aggrTask.getProjectId();

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
        collection.insertOne(doc(query).append("overAverage", overAverage));
    }

}
