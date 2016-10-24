package com.xz.examscore.services;

import com.hyd.simplecache.SimpleCache;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.ajiaedu.common.mongo.MongoUtils;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;
import static com.xz.examscore.util.Mongo.query;

/**
 * @author by fengye on 2016/10/24.
 */
@Service
public class CollegeEntryLevelService {
    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    SimpleCache cache;

    /**
     * 查询本科录取学生列表
     *
     * @param projectId 项目ID
     * @param range     排名范围
     * @param target    排名目标
     * @param minRank   最小名次
     * @param maxRank   最大名次
     *
     * @return 尖子生列表
     */
    public List<Document> getEntryLevelStudent(String projectId, Range range, Target target, int minIndex, int maxIndex) {
        String cacheKey = "college_entry_level:" + projectId + ":" + range
                + ":" + target + ":" + minIndex + ":" + maxIndex;
        return cache.get(cacheKey, () -> {
            MongoCollection<Document> collection = scoreDatabase.getCollection("college_entry_level");
            Document query = query(projectId, range, target)
                    .append("rank", doc("$gte", minIndex).append("$lte", maxIndex));

            Document projection = doc("totalScore", 1).append("rank", 1).append("student", 1).append("college_entry_level", 1).append("dValue", 1);  // 查询结果包含属性

            return new ArrayList<>(MongoUtils.toList(collection.find(query).projection(projection)));
        });
    }
}
