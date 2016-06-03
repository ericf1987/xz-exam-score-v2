package com.xz.services;

import com.hyd.simplecache.SimpleCache;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.bean.Range;
import com.xz.bean.Target;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.xz.util.Mongo.range2Doc;
import static com.xz.util.Mongo.target2Doc;

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
    SimpleCache cache;

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
        return cache.get(cacheKey, () -> {
            MongoCollection<Document> collection = scoreDatabase.getCollection("over_average");
            Document document = collection.find(
                    new Document("project", projectId)
                            .append("range", range2Doc(range))
                            .append("target", target2Doc(target))
            ).first();

            if (document != null) {
                return document.getDouble("overAverage");
            } else {
                LOG.error("找不到超均率, project={}, range={}, target={}", projectId, range, target);
                return 0d;
            }
        });
    }
}
