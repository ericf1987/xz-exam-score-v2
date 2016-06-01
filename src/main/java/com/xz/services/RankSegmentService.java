package com.xz.services;

import com.mongodb.client.MongoDatabase;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.util.Mongo;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * 查询排名分段比率
 *
 * @author yiding_he
 */
@Service
public class RankSegmentService {

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    ProjectConfigService projectConfigService;

    @SuppressWarnings("unchecked")
    public List<Document> getRankSegments(String projectId, Range range, Target target) {

        Document query = Mongo.query(projectId, range, target);
        Document doc = scoreDatabase.getCollection("rank_segment").find(query).first();

        if (doc != null) {
            return (List<Document>) doc.get("rankSegments");
        } else {
            return Collections.emptyList();
        }
    }
}
