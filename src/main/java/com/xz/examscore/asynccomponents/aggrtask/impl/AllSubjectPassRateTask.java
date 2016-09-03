package com.xz.examscore.asynccomponents.aggrtask.impl;

import com.hyd.simplecache.utils.MD5;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.ajiaedu.common.lang.CounterMap;
import com.xz.examscore.asynccomponents.aggrtask.AggrTask;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMessage;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMeta;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.ScoreLevelService;
import com.xz.examscore.services.StudentService;
import com.xz.examscore.services.SubjectService;
import com.xz.examscore.util.Mongo;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;
import static com.xz.ajiaedu.common.report.Keys.ScoreLevel.*;

@Component
@AggrTaskMeta(taskType = "all_subject_pass_rate")
public class AllSubjectPassRateTask extends AggrTask {

    @Autowired
    StudentService studentService;

    @Autowired
    SubjectService subjectService;

    @Autowired
    ScoreLevelService scoreLevelService;

    @Autowired
    MongoDatabase scoreDatabase;

    @Override
    protected void runTask(AggrTaskMessage taskInfo) {
        String projectId = taskInfo.getProjectId();
        Range range = taskInfo.getRange();

        List<String> subjectIds = subjectService.querySubjects(projectId);
        List<String> studentList = studentService.getStudentIds(projectId, range, Target.project(projectId));

        // 计算全科及格和不及格人数和优秀人数以及良好人数
        CounterMap<String> passCounter = new CounterMap<>();

        int subjectCount = subjectIds.size();
        for (String studentId : studentList) {

            int passCount = 0, excellentCount = 0, goodCount = 0, failCount = 0;
            for (String subjectId : subjectIds) {
                String scoreLevel = scoreLevelService.getScoreLevel(projectId, studentId, Target.subject(subjectId));
                if(scoreLevel.equals(Excellent.name())){
                    passCount ++;
                    goodCount ++;
                    excellentCount ++;
                }else if(scoreLevel.equals(Good.name())){
                    passCount ++;
                    goodCount ++;
                }else if(scoreLevel.equals(Pass.name())){
                    passCount ++;
                }else{
                    failCount ++;
                }
            }
            if(!subjectIds.isEmpty()){
                if (subjectCount == passCount) {
                    passCounter.incre("allPass");
                }
                if (subjectCount == failCount) {
                    passCounter.incre("allFail");
                }
                if (subjectCount == excellentCount) {
                    passCounter.incre("allExcellent");
                }
                if (subjectCount == goodCount) {
                    passCounter.incre("allGood");
                }
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
                .append("allGoodRate", allGoodRate)
                .append("md5", MD5.digest(UUID.randomUUID().toString())));

    }
}
