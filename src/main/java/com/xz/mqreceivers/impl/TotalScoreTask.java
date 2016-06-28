package com.xz.mqreceivers.impl;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.mqreceivers.AggrTask;
import com.xz.mqreceivers.Receiver;
import com.xz.mqreceivers.ReceiverInfo;
import com.xz.services.RangeService;
import com.xz.services.ScoreService;
import com.xz.services.StudentService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static com.xz.ajiaedu.common.mongo.MongoUtils.*;
import static com.xz.util.Mongo.range2Doc;
import static com.xz.util.Mongo.target2Doc;

@Component
@ReceiverInfo(taskType = "total_score")
public class TotalScoreTask extends Receiver {

    public static final String[] AGGR_RANGE_NAMES = {
            Range.CLASS, Range.SCHOOL, Range.AREA, Range.CITY, Range.PROVINCE
    };

    @Autowired
    RangeService rangeService;

    @Autowired
    ScoreService scoreService;

    @Autowired
    StudentService studentService;

    @Autowired
    MongoDatabase scoreDatabase;

    @Override
    protected void runTask(AggrTask aggrTask) {

        // 获取任务信息（每个考生一个任务）
        String projectId = aggrTask.getProjectId();
        Target target = aggrTask.getTarget();

        // 单个考生的题目的分已经在 score 集合有了，无需再统计
        if (!target.match(Target.QUEST)) {
            aggrStudentScores(projectId, target);
        }

        // 统计考生群体（班级/学校/总体）的分数
        for (String rangeName : AGGR_RANGE_NAMES) {
            aggrNonStudentScores(projectId, target, rangeName);
        }
    }

    private void aggrNonStudentScores(
            String projectId, Target target, String aggrRangeName) {

        String collectionName = scoreService.getTotalScoreCollection(projectId, target);
        List<Range> aggrRanges = rangeService.queryRanges(projectId, aggrRangeName);

        for (Range aggrRange : aggrRanges) {
            if (target.match(Target.QUEST)) {
                aggrNonStudentScoresFromDetail(projectId, target, aggrRange);
            } else {
                aggrNonStudentScores(projectId, collectionName, target, aggrRange);
            }
        }
    }

    // 从 score 成绩详情中统计题目总分
    private void aggrNonStudentScoresFromDetail(
            String projectId, Target target, Range aggrRange) {

        MongoCollection<Document> c = scoreDatabase.getCollection("score");

        AggregateIterable<Document> aggregate = c.aggregate(Arrays.asList(
                $match(doc("project", projectId).append(
                        aggrRange.getName(), aggrRange.getId()).append("quest", target.getId().toString())),
                $group(doc("_id", null).append("totalScore", doc("$sum", "$score")))
        ));

        Document aggregateResult = aggregate.first();
        if (aggregateResult != null) {
            Double score = aggregateResult.getDouble("totalScore");
            Range parent = rangeService.getParentRange(projectId, aggrRange);
            scoreService.saveTotalScore(projectId, aggrRange, parent, target, score, null);
        }
    }

    // 统计非学生的总分
    private void aggrNonStudentScores(
            String projectId, String collectionName, Target target, Range aggrRange) {

        Document match = doc("project", projectId)
                .append("parent", range2Doc(aggrRange))
                .append("target", target2Doc(target));

        Document group = doc("_id", null).append("totalScore", doc("$sum", "$totalScore"));

        AggregateIterable<Document> aggregate = scoreDatabase
                .getCollection(collectionName)
                .aggregate(Arrays.asList($match(match), $group(group)));

        Document aggregateResult = aggregate.first();
        if (aggregateResult != null) {
            Range parent = rangeService.getParentRange(projectId, aggrRange);
            Double score = aggregateResult.getDouble("totalScore");
            scoreService.saveTotalScore(projectId, aggrRange, parent, target, score, null);
        }
    }

    //////////////////////////////////////////////////////////////

    private void aggrStudentScores(String projectId, Target target) {
        List<Range> studentRanges = rangeService.queryRanges(projectId, Range.STUDENT);

        if (target.match(Target.SUBJECT) || target.match(Target.PROJECT)) {
            aggrStudentSubjectProjectScores(projectId, studentRanges, target);
        }
    }

    // 统计单个学生的科目/项目总分
    private void aggrStudentSubjectProjectScores(String projectId, List<Range> studentRanges, Target target) {
        Document group = new Document()
                .append("_id", null)
                .append("totalScore", new Document("$sum", "$score"));

        MongoCollection<Document> c = scoreDatabase.getCollection("score");

        for (Range studentRange : studentRanges) {
            String studentId = studentRange.getId();
            Document student = studentService.findStudent(projectId, studentId);
            String classId = student.getString("class");

            // 统计单个考生的科目/项目总分
            AggregateIterable<Document> aggregate = c.aggregate(Arrays.asList(
                    doc("$match", doc("project", projectId)
                            .append("student", studentId)
                            .append(target.getName(), target.getId())),
                    doc("$group", group)
            ));
            Document aggregateResult = aggregate.first();

            // 如果缺考则会导致 aggregate() 没有返回值
            if (aggregateResult != null) {
                Double score = aggregateResult.getDouble("totalScore");
                Document extra = doc("class", student.get("class")).append("school", student.get("school"))
                        .append("area", student.get("area")).append("city", student.get("city"))
                        .append("province", student.get("province"));
                scoreService.saveTotalScore(projectId, studentRange, Range.clazz(classId), target, score, extra);
            }
        }
    }

}
