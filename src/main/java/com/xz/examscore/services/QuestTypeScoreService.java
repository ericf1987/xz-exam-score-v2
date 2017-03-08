package com.xz.examscore.services;

import com.hyd.simplecache.SimpleCache;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.xz.ajiaedu.common.lang.CollectionUtils;
import com.xz.examscore.bean.Range;
import com.xz.examscore.cache.ProjectCacheManager;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;
import static com.xz.ajiaedu.common.mongo.MongoUtils.toList;
import static com.xz.examscore.util.Mongo.range2Doc;

@Service
public class QuestTypeScoreService {

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    ProjectCacheManager projectCacheManager;

    /**
     * 查询题型得分
     *
     * @param projectId   项目ID
     * @param range       查询范围
     * @param questTypeId 题型ID
     *
     * @return 题型得分
     */
    public double getQuestTypeScore(String projectId, Range range, String questTypeId) {
        if (range.match(Range.STUDENT)) {
            return getStudentQuestTypeScore(projectId, range.getId(), questTypeId);
        } else {
            return getNonStudentQuestTypeScore(projectId, range, questTypeId);
        }
    }

    private double getNonStudentQuestTypeScore(String projectId, Range range, String questTypeId) {
        Document doc = scoreDatabase.getCollection("quest_type_score_average").find(
                doc("project", projectId).append("range", range2Doc(range)).append("questType", questTypeId)
        ).first();

        return doc != null ? doc.getDouble("average") : 0d;
    }

    private double getStudentQuestTypeScore(String projectId, String studentId, String questTypeId) {
        Document doc = scoreDatabase.getCollection("quest_type_score").find(
                doc("project", projectId).append("student", studentId).append("questType", questTypeId)
        ).first();

        return doc != null ? doc.getDouble("score") : 0d;
    }

    //查询学生维度的题型得分列表
    public ArrayList<Document> getStudentQuestTypeScoreList(String projectId, Range range) {
        String cacheKey = "quest_type_score:" + projectId + ":" + range;

        SimpleCache simpleCache = projectCacheManager.getProjectCache(projectId);

        return simpleCache.get(cacheKey, () -> {
            FindIterable<Document> documents = scoreDatabase.getCollection("quest_type_score").find(doc("project", projectId).append("class", range.getId()));
            return CollectionUtils.asArrayList(toList(documents));
        });
    }

    //查询非学生维度的题型得分列表
    public ArrayList<Document> getNonStudentQuestTypeScoreList(String projectId, Range range) {
        String cacheKey = "quest_type_score_average:" + projectId + ":" + range;

        SimpleCache simpleCache = projectCacheManager.getProjectCache(projectId);

        return simpleCache.get(cacheKey, () -> {
            FindIterable<Document> documents = scoreDatabase.getCollection("quest_type_score_average").find(doc("project", projectId).append("range", range2Doc(range)));
            return CollectionUtils.asArrayList(toList(documents));
        });
    }
}
