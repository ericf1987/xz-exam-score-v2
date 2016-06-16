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

import static com.xz.ajiaedu.common.mongo.MongoUtils.$set;
import static com.xz.ajiaedu.common.mongo.MongoUtils.UPSERT;
import static com.xz.util.Mongo.*;

/**
 * T分值相关
 *
 * @author zhaorenwu
 */
@Service
public class TScoreService {

    static final Logger LOG = LoggerFactory.getLogger(TScoreService.class);

    @Autowired
    SimpleCache cache;

    @Autowired
    MongoDatabase scoreDatabase;

    /**
     * 查询T分值
     *
     * @param projectId 考试项目id
     * @param target    目标
     * @param range     范围
     *
     * @return T分值
     */
    public double queryTScore(String projectId, Target target, Range range) {
        String cacheKey = "t_score_value:" + projectId + ":" + range;
        return cache.get(cacheKey, () -> {
            MongoCollection<Document> collection = scoreDatabase.getCollection("t_score");
            Document document = collection.find(
                    new Document("project", projectId)
                            .append("range", range2Doc(range))
                            .append("target", target2Doc(target))
            ).first();

            if (document != null) {
                return document.getDouble("tScore");
            } else {
                LOG.error("找不到T分值, project={}, range={}, target={}", projectId, range, target);
                return 0d;
            }
        });
    }

    /**
     * 保存T分值
     *
     * @param projectId 考试项目id
     * @param target    目标
     * @param range     范围
     * @param tscore    T分值
     */
    public void saveTScore(String projectId, Target target, Range range, double tscore) {

        scoreDatabase.getCollection("t_score").updateOne(
                query(projectId, range, target), $set("tScore", tscore), UPSERT);

        String cacheKey = "t_score_value:" + projectId + ":" + range;
        cache.delete(cacheKey);
    }
}
