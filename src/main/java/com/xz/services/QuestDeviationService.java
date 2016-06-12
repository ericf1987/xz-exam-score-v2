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
 * 试题区分度
 *
 * @author zhaorenwu
 */
@Service
public class QuestDeviationService {

    static final Logger LOG = LoggerFactory.getLogger(QuestDeviationService.class);

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    SimpleCache cache;

    /**
     * 查询试题区分度
     *
     * @param projectId 项目ID
     * @param range     范围
     * @param questId   试题id
     *
     * @return 试题区分度
     */
    public double getQuestDeviation(String projectId, String questId, Range range) {
        String cacheKey = "quest_deviations:" + projectId + ":" + questId + ":" + range;
        return cache.get(cacheKey, () -> {
            MongoCollection<Document> collection = scoreDatabase.getCollection("quest_deviation");
            Document document = collection.find(
                    new Document("project", projectId)
                            .append("quest", questId)
                            .append("range", range2Doc(range))
            ).first();

            if (document != null) {
                return document.getDouble("deviation");
            } else {
                LOG.error("找不到试题区分度, project={}, questId={}, range={}", projectId, questId, range);
                return 0d;
            }
        });
    }
}
