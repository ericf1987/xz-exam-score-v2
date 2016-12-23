package com.xz.examscore.asynccomponents.aggrtask.impl;

import com.hyd.simplecache.utils.MD5;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.examscore.asynccomponents.aggrtask.AggrTask;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMessage;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMeta;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.ScoreService;
import com.xz.examscore.services.SubjectCombinationService;
import com.xz.examscore.services.SubjectService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;
import static com.xz.examscore.util.Mongo.range2Doc;
import static com.xz.examscore.util.Mongo.target2Doc;

/**
 * 文理综合科目合计，结果存入 total_score_combined 集合
 * 之所以不存入 total_score 集合，是因为如果这样做的话就会存在重复成绩，
 * 遍历考生所有科目得分纪录的总和就会大于其考试总分
 */
@Component
@AggrTaskMeta(taskType = "combined_total_score")
public class CombinedSubjectScoreTask extends AggrTask {

    @Autowired
    ScoreService scoreService;

    @Autowired
    SubjectService subjectService;

    @Autowired
    MongoDatabase scoreDatabase;

    @Override
    protected void runTask(AggrTaskMessage taskInfo) {
        String projectId = taskInfo.getProjectId();
        String studentId = taskInfo.getRange().getId();

        // 查询科目列表，对文理科目得分求和
        List<String> subjectIds = subjectService.querySubjects(projectId);
        double w = 0, l = 0;
        for (String subjectId : subjectIds) {
            w += SubjectCombinationService.isW(subjectId) ? scoreService.getSubjectScore(projectId, studentId, subjectId) : 0;
            l += SubjectCombinationService.isL(subjectId) ? scoreService.getSubjectScore(projectId, studentId, subjectId) : 0;
        }

        saveScore(projectId, studentId, l, "004005006");    // 保存文科合计分数
        saveScore(projectId, studentId, w, "007008009");    // 保存理科合计分数
    }

    private void saveScore(String projectId, String studentId, double score, String subjectId) {
        Document lQuery = doc("project", projectId)
                .append("target", target2Doc(Target.subject(subjectId)))
                .append("range", range2Doc(Range.student(studentId)));

        MongoCollection<Document> totalScore = scoreDatabase.getCollection("total_score_combined");
        totalScore.deleteMany(lQuery);
        totalScore.insertOne(doc(lQuery).append("totalScore", score).append("md5", MD5.digest(UUID.randomUUID().toString())));
    }

}
