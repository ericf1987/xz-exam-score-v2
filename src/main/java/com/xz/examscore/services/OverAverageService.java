package com.xz.examscore.services;

import com.hyd.simplecache.SimpleCache;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.cache.ProjectCacheManager;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.xz.examscore.util.Mongo.range2Doc;
import static com.xz.examscore.util.Mongo.target2Doc;

/**
 * 超均率
 *
 * @author zhaorenwu
 */
@Service
public class OverAverageService {

    public static final Logger LOG = LoggerFactory.getLogger(OverAverageService.class);

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    ProjectCacheManager projectCacheManager;

    /**
     * 查询超均率
     *
     * @param projectId 考试项目id
     * @param range     范围
     * @param target    目标
     *
     * @return  超均率
     */
    public double getOverAverage(String projectId, Range range, Target target) {
        String cacheKey = "over_average:" + projectId + ":" + range + ":" + target;

        SimpleCache simpleCache = projectCacheManager.getProjectCache(projectId);

        return simpleCache.get(cacheKey, () -> {
            MongoCollection<Document> collection = scoreDatabase.getCollection("over_average");
            Document document = collection.find(
                    new Document("project", projectId)
                            .append("range", range2Doc(range))
                            .append("target", target2Doc(target))
            ).first();

            if (document != null) {
                return document.getDouble("overAverage");
            } else {
                return 0d;
            }
        });
    }
}
