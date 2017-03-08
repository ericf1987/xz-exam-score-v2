package com.xz.examscore.services;

import com.hyd.simplecache.SimpleCache;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.cache.ProjectCacheManager;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;
import static com.xz.examscore.util.Mongo.range2Doc;
import static com.xz.examscore.util.Mongo.target2Doc;

/**
 * @author by fengye on 2016/12/21.
 */
@Service
public class ScoreRateService {
    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    ProjectCacheManager projectCacheManager;

    public double getScoreRate(String projectId, Range range, Target target) {
        String cacheKey = "score_rate:" + projectId + ":" + range + ":" + target;

        SimpleCache simpleCache = projectCacheManager.getProjectCache(projectId);

        return simpleCache.get(cacheKey, () -> {
            MongoCollection<Document> collection = scoreDatabase.getCollection("score_rate");
            Document query = doc("project", projectId).append("range", range2Doc(range)).append("target", target2Doc(target));
            Document document = collection.find(query).first();
            return document == null ? 0d : document.getDouble("scoreRate");
        });
    }
}
