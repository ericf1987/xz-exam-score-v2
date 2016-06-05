package com.xz.services;

import com.hyd.simplecache.SimpleCache;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.ajiaedu.common.mongo.MongoUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;

/**
 * (description)
 * created at 16/05/11
 *
 * @author yiding_he
 */
@Service
public class QuestService {

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    SimpleCache simpleCache;

    public Document findQuest(String projectId, String questId) {
        String cacheKey = "quest:" + projectId + ":" + questId;

        return simpleCache.get(cacheKey, () ->
                scoreDatabase.getCollection("quest_list")
                        .find(doc("questId", questId).append("project", projectId)).first());
    }

    public List<Document> getQuests(String projectId, String subjectId, boolean isObjective) {
        return MongoUtils.toList(
                scoreDatabase.getCollection("quest_list").find(
                        doc("project", projectId)
                                .append("subject", subjectId)
                                .append("isObjective", isObjective)
                ));
    }

    public List<Document> getQuests(String projectId, String subjectId) {
        return MongoUtils.toList(
                scoreDatabase.getCollection("quest_list").find(
                        doc("project", projectId).append("subject", subjectId)));
    }

    public List<Document> getQuests(String projectId) {
        return MongoUtils.toList(
                scoreDatabase.getCollection("quest_list").find(doc("project", projectId)));
    }

    /**
     * 查询项目中的主观题/客观题列表
     *
     * @param projectId   项目ID
     * @param isObjective 是否是客观题
     *
     * @return 题目列表
     */
    public List<Document> getQuests(String projectId, boolean isObjective) {
        return MongoUtils.toList(
                scoreDatabase.getCollection("quest_list").find(
                        doc("project", projectId).append("isObjective", isObjective)));
    }

    //////////////////////////////////////////////////////////////

    /**
     * 更新项目中的所有题目记录（会删除旧的记录）
     *
     * @param projectId 项目ID
     * @param quests    题目列表
     */
    public void saveProjectQuests(String projectId, List<Document> quests) {
        MongoCollection<Document> collection = scoreDatabase.getCollection("quest_list");
        collection.deleteMany(doc("project", projectId));
        collection.insertMany(quests);
    }

}
