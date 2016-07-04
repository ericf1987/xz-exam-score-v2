package com.xz.mqreceivers.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.ajiaedu.common.lang.CounterMap;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.mqreceivers.AggrTask;
import com.xz.mqreceivers.Receiver;
import com.xz.mqreceivers.ReceiverInfo;
import com.xz.services.ScoreLevelService;
import com.xz.services.StudentService;
import com.xz.services.SubjectService;
import com.xz.util.Mongo;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;
import static com.xz.ajiaedu.common.report.Keys.ScoreLevel.*;

@Component
@ReceiverInfo(taskType = "all_subject_pass_rate")
public class AllSubjectPassRateTask extends Receiver {

    @Autowired
    StudentService studentService;

    @Autowired
    SubjectService subjectService;

    @Autowired
    ScoreLevelService scoreLevelService;

    @Autowired
    MongoDatabase scoreDatabase;

    @Override
    protected void runTask(AggrTask aggrTask) {
        String projectId = aggrTask.getProjectId();
        Range range = aggrTask.getRange();

        List<String> subjectIds = subjectService.querySubjects(projectId);
        List<String> studentList = studentService.getStudentIds(projectId, range, Target.project(projectId));

        // 计算全科及格和不及格人数和优秀人数以及良好人数
        CounterMap<String> passCounter = new CounterMap<>();

        for (String studentId : studentList) {
            boolean allPass = true, allFail = true, allExcellent = true, allGood = true;

            for (String subjectId : subjectIds) {
                String scoreLevel = scoreLevelService.getScoreLevel(projectId, studentId, Target.subject(subjectId));
                if (!scoreLevel.equals(Pass.name())) {
                    allPass = false;
                }
                if (!scoreLevel.equals(Excellent.name())) {
                    allExcellent = false;
                }
                if (!scoreLevel.equals(Good.name())) {
                    allGood = false;
                }
                if (!scoreLevel.equals(Fail.name())) {
                    allFail = false;
                }
            }

            if (allPass) {
                passCounter.incre("allPass");
            }
            if (allFail) {
                passCounter.incre("allFail");
            }
            if (allExcellent) {
                passCounter.incre("allExcellent");
            }
            if (allGood) {
                passCounter.incre("allGood");
            }
        }

        // 计算相应比例
        double allPassRate = (double) (passCounter.getCount("allPass")) / studentList.size();
        double allFailRate = (double) (passCounter.getCount("allFail")) / studentList.size();
        double allExcellentRate = (double) (passCounter.getCount("allExcellent")) / studentList.size();
        double allGoodRate = (double) (passCounter.getCount("allGood")) / studentList.size();

        Document query = doc("project", projectId).append("range", Mongo.range2Doc(range));
        MongoCollection<Document> c = scoreDatabase.getCollection("all_subject_pass_rate");
        c.deleteMany(query);
        c.insertOne(doc(query)
                .append("allPassRate", allPassRate)
                .append("allFailRate", allFailRate)
                .append("allExcellentRate", allExcellentRate)
                .append("allGoodRate", allGoodRate));
    }
}
