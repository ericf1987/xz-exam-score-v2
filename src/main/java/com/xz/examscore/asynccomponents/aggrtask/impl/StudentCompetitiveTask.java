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
import com.xz.examscore.services.ScoreService;
import com.xz.examscore.services.StudentService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.xz.ajiaedu.common.mongo.MongoUtils.$set;
import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;
import static com.xz.examscore.util.Mongo.range2Doc;
import static com.xz.examscore.util.Mongo.target2Doc;

/**
 * @author by fengye on 2016/12/29.
 */
@AggrTaskMeta(taskType = "student_competitive")
@Component
public class StudentCompetitiveTask extends AggrTask{

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    StudentService studentService;

    @Autowired
    ScoreService scoreService;

    public static final int PIECE = 10;

    @Override
    protected void runTask(AggrTaskMessage taskInfo) {
        String projectId = taskInfo.getProjectId();
        Range range = taskInfo.getRange();
        Target target = taskInfo.getTarget();
        List<String> studentIds = studentService.getStudentIds(projectId, range, target);
        //按照学生的全科总分高低进行排序
        Collections.sort(studentIds, (String s1, String s2) -> {
            Double score1 = scoreService.getScore(projectId, Range.student(s1), Target.project(projectId));
            Double score2 = scoreService.getScore(projectId, Range.student(s2), Target.project(projectId));
            return score2.compareTo(score1);
        });

        int quotient = studentIds.size() / PIECE;
        int lastHead = 0;
        for(int i = 1;i <= quotient;i++){
            int head = (i - 1) * PIECE;
            int tail = head + PIECE;
            List<String> sub = studentIds.subList(head, tail);
            handleData(projectId, range, sub, target, head + 1, tail);
            lastHead = tail;
        }
        List<String> _sub = studentIds.subList(lastHead, studentIds.size());
        handleData(projectId, range, _sub, target, lastHead, studentIds.size());
    }

    private void handleData(String projectId, Range range, List<String> studentIds, Target target, int head, int tail) {
        double totalScore = 0;
        int count = studentIds.size();
        for(String studentId : studentIds){
            double score = scoreService.getScore(projectId, Range.student(studentId), target);
            totalScore += score;
        }
        double average = count == 0 ? 0 : totalScore / studentIds.size();
        Document query = doc("project", projectId)
                .append("range", range2Doc(range))
                .append("target", target2Doc(target))
                .append("startIndex", head).append("endIndex", tail)
                .append("section", head + "-" + tail);
        MongoCollection<Document> collection = scoreDatabase.getCollection("student_competitive");

        UpdateResult result = collection.updateMany(query, $set(doc("average", average)));
        if (result.getMatchedCount() == 0) {
            collection.insertOne(query.append("average", average).append("md5", MD5.digest(UUID.randomUUID().toString())));
        }
    }
}
