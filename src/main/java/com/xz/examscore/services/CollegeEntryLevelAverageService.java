package com.xz.examscore.services;

import com.hyd.appserver.utils.StringUtils;
import com.hyd.simplecache.SimpleCache;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.cache.ProjectCacheManager;
import com.xz.examscore.util.Mongo;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author by fengye on 2016/12/23.
 */
@Service
public class CollegeEntryLevelAverageService {
    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    ProjectConfigService projectConfigService;

    @Autowired
    ProjectCacheManager projectCacheManager;

    public double getAverage(String projectId, Range range, Target target, String entryLevelKey) {
        String cacheKey = "college_entry_level_average:" + projectId + ":" + range + ":" + target + ":" + entryLevelKey;

        SimpleCache simpleCache = projectCacheManager.getProjectCache(projectId);

        return simpleCache.get(cacheKey, () -> {
            MongoCollection<Document> collection = scoreDatabase.getCollection("college_entry_level_average");
            Document query = Mongo.query(projectId, range, target);
            if (!StringUtils.isBlank(entryLevelKey)) {
                query.append("college_entry_level.level", entryLevelKey);
            }
            Document first = collection.find(query).first();
            return first.getDouble("average") == null ? 0d : first.getDouble("average");
        });

    }

    public void clearAverage(String projectId, Range range, Target target, String entryLevelKey) {
        MongoCollection<Document> collection = scoreDatabase.getCollection("college_entry_level_average");
        Document query = Mongo.query(projectId, range, target);
        if (!StringUtils.isBlank(entryLevelKey)) {
            query.append("college_entry_level.level", entryLevelKey);
        }
        collection.deleteMany(query);
    }
}
