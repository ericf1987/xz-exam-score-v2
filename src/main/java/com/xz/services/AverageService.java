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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;
import static com.xz.util.Mongo.range2Doc;
import static com.xz.util.Mongo.target2Doc;

/**
 * (description)
 * created at 16/05/14
 *
 * @author yiding_he
 */
@Service
public class AverageService {

    static final Logger LOG = LoggerFactory.getLogger(AverageService.class);

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    SubjectService subjectService;

    @Autowired
    SimpleCache cache;

    /**
     * 查询指定范围的所有科目平均分总和
     *
     * @param projectId 项目ID
     * @param range     范围
     *
     * @return 各科平均分总和
     */
    public Map<String, Double> getAllSubjectAverage(String projectId, Range range) {
        List<String> subjectIds = subjectService.querySubjects(projectId);
        Map<String, Double> result = new HashMap<>();

        for (String subjectId : subjectIds) {
            result.put(subjectId, getAverage(projectId, range, Target.subject(subjectId)));
        }

        return result;
    }

    /**
     * 判断指定目标对象名称是否已经有平均值
     *
     * @param projectId     考试项目id
     * @param targetName    目标名称
     *
     * @return  是否有平均值
     */
    public boolean isExistAverage(String projectId, String targetName) {
        String cacheKey = "average_status" + projectId + ":" + targetName;
        return cache.get(cacheKey, () -> {
            MongoCollection<Document> averageCollection = scoreDatabase.getCollection("average");
            return averageCollection.find(
                    doc("project", projectId).append("target.name", targetName)
            ).first() != null;
        });
    }

    /**
     * 查询平均分
     *
     * @param projectId 项目ID
     * @param range     范围
     * @param target    目标
     *
     * @return 平均分
     */
    public double getAverage(String projectId, Range range, Target target) {
        String cacheKey = "average:" + projectId + ":" + range + ":" + target;
        return cache.get(cacheKey, () -> {
            return getAverage0(projectId, range, target);
        });
    }

    private double getAverage0(String projectId, Range range, Target target) {
        MongoCollection<Document> averageCollection = scoreDatabase.getCollection("average");
        Document document = averageCollection.find(
                new Document("project", projectId)
                        .append("range", range2Doc(range))
                        .append("target", target2Doc(target))
        ).first();

        if (document != null) {
            return document.getDouble("average");
        } else {
            return 0d;
        }
    }

    public void deleteCache(String projectId, Range range, Target target) {
        String cacheKey = "average:" + projectId + ":" + range + ":" + target;
        cache.delete(cacheKey);
    }
}
