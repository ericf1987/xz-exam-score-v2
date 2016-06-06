package com.xz.mqreceivers.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import com.xz.ajiaedu.common.redis.Redis;
import com.xz.bean.ProjectConfig;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.mqreceivers.AggrTask;
import com.xz.mqreceivers.Receiver;
import com.xz.mqreceivers.ReceiverInfo;
import com.xz.services.ProjectConfigService;
import com.xz.services.ScoreService;
import com.xz.services.StudentService;
import com.xz.services.TargetService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.xz.ajiaedu.common.mongo.MongoUtils.*;
import static com.xz.util.Mongo.query;

/**
 * 生成排名记录
 */
@ReceiverInfo(taskType = "score_map")
@Component
public class ScoreMapTask extends Receiver {

    public static final String[] RANGE_NAMES = {Range.CLASS, Range.SCHOOL, Range.AREA, Range.CITY, Range.PROVINCE};

    public static final String[] NON_QUEST_TARGET_NAMES = new String[]{Target.SUBJECT, Target.SUBJECT_OBJECTIVE, Target.PROJECT, Target.POINT, Target.QUEST_TYPE};

    @Autowired
    ScoreService scoreService;

    @Autowired
    StudentService studentService;

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    TargetService targetService;

    @Autowired
    ProjectConfigService projectConfigService;

    @Autowired
    Redis redis;

    @Override
    protected void runTask(AggrTask aggrTask) {

        String projectId = aggrTask.getProjectId();
        Range studentRange = aggrTask.getRange();
        ProjectConfig projectConfig = projectConfigService.getProjectConfig(projectId);
        String studentId = studentRange.getId();
        Document student = studentService.findStudent(projectId, studentId);


        // 对哪些分数进行排名
        List<Target> targets = targetService.queryTargets(projectId,
                Target.QUEST, Target.SUBJECT, Target.SUBJECT_OBJECTIVE, Target.PROJECT, Target.POINT, Target.QUEST_TYPE);

        // 如果项目需要对文综理综进行整合（考试本身没有这两个科目），则额外
        // 添加文综理综的排名统计（总分统计在 CombinedSubjectScoreDispatcher 里已经做了）
        if (projectConfig.isCombineCategorySubjects()) {
            targets.add(Target.subject("004005006"));   // 理综
            targets.add(Target.subject("007008009"));   // 文综
        }

        MongoCollection<Document> collection = scoreDatabase.getCollection("score_map");

        // 查询学生题目得分，并累加到数据库
        List<Document> questScores = scoreService.getStudentScores(projectId, studentId, Target.QUEST);
        for (Document questScore : questScores) {
            Target questTarget = Target.quest(questScore.getString("quest"));
            double score = questScore.getDouble("score");
            dispatchScore(projectId, student, collection, questTarget, score);
        }

        // 查询学生非题目得分，并累加到数据库
        for (String targetName : NON_QUEST_TARGET_NAMES) {
            List<Document> nonQuestScores = scoreService.getStudentScores(projectId, studentId, targetName);
            for (Document nonQuestScore : nonQuestScores) {
                Target questTarget = Target.parse((Document) nonQuestScore.get("target"));
                double score = nonQuestScore.getDouble("totalScore");
                dispatchScore(projectId, student, collection, questTarget, score);
            }
        }
    }

    private void dispatchScore(
            String projectId, Document student, MongoCollection<Document> c, Target target, double score) {

        for (String rangeName : RANGE_NAMES) {
            String rangeId = student.getString(rangeName);
            Range range = new Range(rangeName, rangeId);
            Document query = query(projectId, range, target);

            // 尝试 $inc，如果失败（modifiedCount = 0）则执行 $push
            UpdateResult updateResult = c.updateOne(
                    query.append("scoreMap", $elemMatch("score", score)), $inc("scoreMap.$.count", 1));

            if (updateResult.getModifiedCount() > 0) {
                continue;
            }

            c.updateOne(query, $push("scoreMap", doc("score", score).append("count", 1)), UPSERT);
        }
    }

}
