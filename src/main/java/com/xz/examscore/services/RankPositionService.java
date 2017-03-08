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

import java.util.ArrayList;
import java.util.List;

import static com.xz.examscore.util.Mongo.range2Doc;
import static com.xz.examscore.util.Mongo.target2Doc;

/**
 * 中位数相关
 *
 * @author zhaorenwu
 */

@Service
public class RankPositionService {

    static final Logger LOG = LoggerFactory.getLogger(RankPositionService.class);

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    ProjectCacheManager projectCacheManager;

    /**
     * 查询中位数
     *
     * @param projectId 项目ID
     * @param range     范围
     * @param target    目标
     *
     * @return 中位数
     */
    @SuppressWarnings("unchecked")
    public List<Document> getRankPositions(String projectId, Range range, Target target) {
        String cacheKey = "rank_positions:" + projectId + ":" + range + ":" + target;

        SimpleCache simpleCache = projectCacheManager.getProjectCache(projectId);

        return simpleCache.get(cacheKey, () -> {
            MongoCollection<Document> averageCollection = scoreDatabase.getCollection("rank_position");
            Document document = averageCollection.find(
                    new Document("project", projectId)
                            .append("range", range2Doc(range))
                            .append("target", target2Doc(target))
            ).first();

            if (document != null) {
                return new ArrayList<>(document.get("positions", List.class));
            } else {
                LOG.error("找不到中位数, project={}, range={}, target={}", projectId, range, target);
                return new ArrayList<>();
            }
        });
    }
}
