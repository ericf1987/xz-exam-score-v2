package com.xz.services;

import com.alibaba.fastjson.JSON;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.bean.Range;
import com.xz.bean.Target;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public double getTotalScore(String projectId, Range range, Target target) {
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
