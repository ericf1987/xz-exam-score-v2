package com.xz.examscore.services;

import com.hyd.simplecache.SimpleCache;
import com.hyd.simplecache.utils.MD5;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.examscore.cache.ProjectCacheManager;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.xz.ajiaedu.common.lang.CollectionUtils.asArrayList;
import static com.xz.ajiaedu.common.mongo.MongoUtils.*;

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
    ProjectCacheManager projectCacheManager;

    public Document findQuest(String projectId, String questId) {
        String cacheKey = "quest:" + projectId + ":" + questId;

        SimpleCache simpleCache = projectCacheManager.getProjectCache(projectId);

        return simpleCache.get(cacheKey, () ->
                scoreDatabase.getCollection("quest_list")
                        .find(doc("questId", questId).append("project", projectId)).first());
    }

    public List<Document> getQuests(String projectId, String subjectId, boolean isObjective) {
        return toList(
                scoreDatabase.getCollection("quest_list").find(
                        doc("project", projectId)
                                .append("subject", subjectId)
                                .append("isObjective", isObjective)
                ));
    }

    public List<Document> getQuests(String projectId, List<String> subjectIds, boolean isObjective) {
        return toList(
                scoreDatabase.getCollection("quest_list").find(
                        doc("project", projectId)
                                .append("subject", $in(subjectIds))
                                .append("isObjective", isObjective)
                ));
    }

    public List<Document> getQuests(String projectId, String subjectId) {
        return toList(
                scoreDatabase.getCollection("quest_list").find(
                        doc("project", projectId).append("subject", subjectId)));
    }

    public List<Document> getQuestsByQuestType(String projectId, String questType) {
        return toList(scoreDatabase.getCollection("quest_list").find(
                doc("project", projectId).append("questType", questType)
        ));
    }

    public List<Document> getQuests(String projectId) {
        return toList(
                scoreDatabase.getCollection("quest_list").find(doc("project", projectId)));
    }

    /**
     * 根据知识点和能力层级查询题目
     *
     * @param projectId 项目ID
     * @param point     知识点
     * @param level     能力层级
     *
     * @return 相关题目
     */
    public List<Document> getQuests(String projectId, String point, String level) {
        String cacheKey = "quests_by_pointlevel:" + projectId + ":" + point + ":" + level;

        SimpleCache simpleCache = projectCacheManager.getProjectCache(projectId);

        return simpleCache.get(cacheKey, () -> {
            Document query = doc("project", projectId).append("points." + point, level);
            return asArrayList(toList(scoreDatabase.getCollection("quest_list").find(query)));
        });
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
        return toList(
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

        if (!quests.isEmpty()) {
            collection.insertMany(quests);
        }
    }

    public void clearQuests(String projectId, String subjectId) {
        MongoCollection<Document> collection = scoreDatabase.getCollection("quest_list");
        collection.deleteMany(doc("project", projectId).append("subject", subjectId));
    }

    public void saveQuest(Document quest) {
        MongoCollection<Document> collection = scoreDatabase.getCollection("quest_list");
        collection.deleteMany(doc("project", quest.getString("project"))
                .append("subject", quest.getString("subject"))
                .append("questNo", quest.getString("questNo"))
        );
        quest.append("md5", MD5.digest(UUID.randomUUID().toString()));
        collection.insertOne(quest);
    }

    public Document findQuest(String projectId, String subject, String questNo) {
        String cacheKey = "quest_by_no:" + projectId + ":" + subject + ":" + questNo;

        SimpleCache simpleCache = projectCacheManager.getProjectCache(projectId);

        return simpleCache.get(cacheKey, () -> {
            MongoCollection<Document> collection = scoreDatabase.getCollection("quest_list");

            Document query = doc("project", projectId)
                    .append("subject", subject)
                    .append("questNo", questNo);

            return collection.find(query).first();
        });
    }

    //获取拆分科目后对应的题目
    public Document findQuest(String projectId, List<String> subjectIds, String questNo){
        String cacheKey = "quest_by_no_in_subject:" + projectId + ":" + subjectIds.toString() + ":" + questNo;

        SimpleCache simpleCache = projectCacheManager.getProjectCache(projectId);

        return simpleCache.get(cacheKey, () -> {
            MongoCollection<Document> collection = scoreDatabase.getCollection("quest_list");

            Document query = doc("project", projectId)
                    .append("subject", $in(subjectIds))
                    .append("questNo", questNo);

            return collection.find(query).first();
        });
    }

    // 保存选择题答案
    public void saveQuestItems(String projectId, String questId, List<String> items) {
        MongoCollection<Document> collection = scoreDatabase.getCollection("quest_list");
        collection.updateMany(doc("project", projectId).append("questId", questId), $set("items", items));
    }
}
