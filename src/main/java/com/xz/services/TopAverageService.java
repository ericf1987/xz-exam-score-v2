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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.xz.ajiaedu.common.mongo.MongoUtils.*;
import static com.xz.util.Mongo.range2Doc;
import static com.xz.util.Mongo.target2Doc;

/**
 * 高分段竞争力分析
 *
 * @author zhaorenwu
 */
@Service
public class TopAverageService {

    static final Logger LOG = LoggerFactory.getLogger(TopAverageService.class);

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    SimpleCache cache;

    /**
     * 查询高分段（各校总分前30%）平均得分
     *
     * @param projectId 项目ID
     * @param target    目标
     * @param range     范围
     *
     * @return 高分段（各校总分前30%）平均得分
     */
    public double getTopAverage(String projectId, Target target, Range range, double percent) {
        String cacheKey = "top_averages:" + projectId + ":" + target + ":" + range + ":" + percent;
        return cache.get(cacheKey, () -> {

            Document query = new Document("project", projectId)
                    .append("target", target2Doc(target))
                    .append("range", range2Doc(range));

            MongoCollection<Document> collection = scoreDatabase.getCollection("top_average");
            List<Document> aggregates = new ArrayList<>(Arrays.asList(
                    $match(query),
                    $unwind("$topAverages"),
                    $match(doc("topAverages.percent", percent))
            ));

            Document document = collection.aggregate(aggregates).first();
            if (document != null) {
                return document.get("topAverages", Document.class).getDouble("average");
            } else {
                LOG.error("找不到高分段平均得分, project={}, target={}, range={}, percent={}",
                        projectId, target, range, percent);
                return 0d;
            }
        });
    }
}
