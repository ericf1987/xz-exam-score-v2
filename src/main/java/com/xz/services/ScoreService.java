package com.xz.services;

import com.alibaba.fastjson.JSON;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.bean.Range;
import com.xz.bean.Target;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;

/**
 * (description)
 * created at 16/05/11
 *
 * @author yiding_he
 */
@Service
public class ScoreService {

    @Autowired
    MongoDatabase scoreDatabase;

    /**
     * 查询科目成绩
     *
     * @param projectId 项目ID
     * @param studentId 学生ID
     * @param subjectId 科目ID
     *
     * @return 成绩
     */
    public double getSubjectScore(String projectId, String studentId, String subjectId) {
        return getScore(projectId, Range.student(studentId), Target.subject(subjectId));
    }

    /**
     * 查询分数
     *
     * @param projectId 项目ID
     * @param range     范围
     * @param target    目标
     *
     * @return 分数
     */
    public double getScore(String projectId, Range range, Target target) {

        // 学生的题目得分从 score 查询，其他得分从 total_score 查询
        if (range.match(Range.STUDENT) && target.match(Target.QUEST)) {
            return getQuestScore(projectId, range.getId(), target.getId().toString());
        } else {
            return getTotalScore(projectId, range, target);
        }
    }

    private double getQuestScore(String projectId, String studentId, String questId) {
        MongoCollection<Document> collection = scoreDatabase.getCollection("score");
        Document query = doc("project", projectId).append("student", studentId).append("quest", questId);
        Document document = collection.find(query).first();
        return document == null ? 0d : document.getDouble("score");
    }

    private double getTotalScore(String projectId, Range range, Target target) {
        MongoCollection<Document> totalScores = scoreDatabase.getCollection("total_score");

        Object targetId = target.getId();
        if (!(targetId instanceof String)) {
            targetId = Document.parse(JSON.toJSONString(targetId));
        }

        Document query = new Document("project", projectId)
                .append("range.name", range.getName()).append("range.id", range.getId())
                .append("target.name", target.getName()).append("target.id", targetId);

        Document totalScoreDoc = totalScores.find(query).first();
        if (totalScoreDoc != null) {
            return totalScoreDoc.getDouble("totalScore");
        } else {
            return 0d;
        }
    }
}
