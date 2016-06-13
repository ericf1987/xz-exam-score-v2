package com.xz.services;

import com.mongodb.client.MongoDatabase;
import com.xz.bean.Range;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;

@Service
public class QuestTypeScoreService {

    @Autowired
    MongoDatabase scoreDatabase;

    /**
     * 查询题型得分
     *
     * @param projectId   项目ID
     * @param range       查询范围
     * @param questTypeId 题型ID
     *
     * @return 题型得分
     */
    public double getQuestTypeScore(String projectId, Range range, String questTypeId) {
        if (range.match(Range.STUDENT)) {
            return getStudentQuestTypeScore(projectId, range.getId(), questTypeId);
        } else {
            return getNonStudentQuestTypeScore(projectId, range, questTypeId);
        }
    }

    private double getNonStudentQuestTypeScore(String projectId, Range range, String questTypeId) {
        Document doc = scoreDatabase.getCollection("quest_type_score_average").find(
                doc("project", projectId).append("range", range).append("questType", questTypeId)
        ).first();

        return doc != null ? doc.getDouble("average") : 0d;
    }

    private double getStudentQuestTypeScore(String projectId, String studentId, String questTypeId) {
        Document doc = scoreDatabase.getCollection("quest_type_score").find(
                doc("project", projectId).append("student", studentId).append("questType", questTypeId)
        ).first();

        return doc != null ? doc.getDouble("score") : 0d;
    }
}
