package com.xz.services;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.bean.Range;
import com.xz.bean.Target;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * (description)
 * created at 16/05/14
 *
 * @author yiding_he
 */
@Service
public class AverageService {

    static final Logger LOG = LoggerFactory.getLogger(AverageService.class);

    @Autowired
    MongoDatabase scoreDatabase;

    public double getAverage(String projectId, Range range, Target target) {
        MongoCollection<Document> totalScoreCollection = scoreDatabase.getCollection("total_score");
        Document document = totalScoreCollection.find(
                new Document("project", projectId)
                        .append("range.name", range.getName())
                        .append("range.id", range.getId())
                        .append("target.name", target.getName())
                        .append("target.id", target.idToParam())
        ).first();

        if (document != null) {
            return document.getDouble("average");
        } else {
            LOG.error("找不到平均分, project={}, range={}, target={}", projectId, range, target);
            return 0d;
        }
    }
}
