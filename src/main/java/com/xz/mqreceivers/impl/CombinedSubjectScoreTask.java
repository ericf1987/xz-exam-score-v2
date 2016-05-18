package com.xz.mqreceivers.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.mqreceivers.AggrTask;
import com.xz.mqreceivers.Receiver;
import com.xz.mqreceivers.ReceiverInfo;
import com.xz.services.ScoreService;
import com.xz.services.SubjectService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;
import static com.xz.util.Mongo.range;
import static com.xz.util.Mongo.target;

/**
 * 文理综合科目合计，结果存入 total_score_combined 集合
 * 之所以不存入 total_score 集合，是因为如果这样做的话就会存在重复成绩，
 * 遍历考生所有科目得分纪录的总和就会大于其考试总分
 */
@Component
@ReceiverInfo(taskType = "combined_total_score")
public class CombinedSubjectScoreTask extends Receiver {

    @Autowired
    ScoreService scoreService;

    @Autowired
    SubjectService subjectService;

    @Autowired
    MongoDatabase scoreDatabase;

    @Override
    protected void runTask(AggrTask aggrTask) {
        String projectId = aggrTask.getProjectId();
        String studentId = aggrTask.getRange().getId();

        // 查询科目列表，对文理科目得分求和
        List<String> subjectIds = subjectService.querySubjects(projectId);
        double w = 0, l = 0;
        for (String subjectId : subjectIds) {
            w += isW(subjectId) ? scoreService.getSubjectScore(projectId, studentId, subjectId) : 0;
            l += isL(subjectId) ? scoreService.getSubjectScore(projectId, studentId, subjectId) : 0;
        }

        saveScore(projectId, studentId, l, "004005006");
        saveScore(projectId, studentId, w, "007008009");
    }

    private void saveScore(String projectId, String studentId, double score, String subjectId) {
        Document lQuery = doc("project", projectId)
                .append("target", target(Target.subject(subjectId)))
                .append("range", range(Range.student(studentId)));

        MongoCollection<Document> totalScore = scoreDatabase.getCollection("total_score_combined");
        totalScore.deleteMany(lQuery);
        totalScore.insertOne(doc(lQuery).append("totalScore", score));
    }

    private boolean isL(String subjectId) {
        return StringUtil.isOneOf(subjectId, "004", "005", "006");
    }

    private boolean isW(String subjectId) {
        return StringUtil.isOneOf(subjectId, "007", "008", "009");
    }
}
