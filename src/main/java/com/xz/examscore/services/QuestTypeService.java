package com.xz.examscore.services;

import com.hyd.simplecache.SimpleCache;
import com.hyd.simplecache.utils.MD5;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import com.xz.ajiaedu.common.beans.dic.QuestType;
import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.examscore.cache.ProjectCacheManager;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static com.xz.ajiaedu.common.mongo.MongoUtils.*;

@Service
public class QuestTypeService {

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    ProjectCacheManager projectCacheManager;

    /**
     * 查询指定的项目中有哪些题型（项目初始化用，从 quest_list 中查询，其他方法从 quest_type_list 中查询）
     *
     * @param projectId 项目ID
     * @return 题型列表
     */
    public List<QuestType> generateQuestTypeList(String projectId) {
        String cacheKey = "quest_type_list:" + projectId;

        SimpleCache simpleCache = projectCacheManager.getProjectCache(projectId);

        return simpleCache.get(cacheKey, () -> {
            ArrayList<QuestType> result = new ArrayList<>();
            MongoCollection<Document> collection = scoreDatabase.getCollection("quest_list");
            Document query = doc("project", projectId).append("questTypeId", $ne(null));

            Consumer<String> getQuestType = questTypeId -> {
                Document q = doc("project", projectId).append("questTypeId", questTypeId);
                Document p = doc("questTypeName", 1).append("subject", 1);
                Document d = collection.find(q).projection(p).first();

                if (d != null) {
                    QuestType t = new QuestType();
                    t.setId(questTypeId);
                    t.setName(d.getString("questTypeName"));
                    t.setSubjectId(d.getString("subject"));
                    result.add(t);
                }
            };

            collection.distinct("questTypeId", query, String.class).forEach(getQuestType);
            return result;
        });
    }

    /**
     * 查询题型信息
     *
     * @param projectId   项目ID
     * @param questTypeId 题型ID
     * @return 题型信息
     */
    public QuestType getQuestType(String projectId, String questTypeId) {
        String cacheKey = "quest_type:" + projectId + ":" + questTypeId;

        SimpleCache simpleCache = projectCacheManager.getProjectCache(projectId);

        return simpleCache.get(cacheKey, () -> {
            MongoCollection<Document> collection = scoreDatabase.getCollection("quest_type_list");
            Document query = doc("project", projectId).append("questTypeId", questTypeId);
            Document projection = doc("questTypeName", 1).append("subject", 1);
            Document result = collection.find(query).projection(projection).first();
            if (result == null) {
                return null;
            } else {
                QuestType questType = new QuestType();
                questType.setId(questTypeId);
                questType.setName(result.getString("questTypeName"));
                questType.setSubjectId(result.getString("subject"));
                return questType;
            }
        });
    }

    /**
     * 查询指定项目的指定科目下的题型列表
     *
     * @param projectId 项目ID
     * @param subjectId 科目ID（可选）
     * @return 题型列表
     */
    public List<QuestType> getQuestTypeList(String projectId, String subjectId) {
        String cacheKey = "subject_quest_type:" + projectId + ":" + subjectId;

        SimpleCache simpleCache = projectCacheManager.getProjectCache(projectId);

        return simpleCache.get(cacheKey, () -> {
            ArrayList<QuestType> result = new ArrayList<>();
            MongoCollection<Document> collection = scoreDatabase.getCollection("quest_type_list");

            Document query = doc("project", projectId);
            if (StringUtil.isNotBlank(subjectId)) {
                query.append("subject", subjectId);
            }

            Document projection = doc("questTypeId", 1).append("questTypeName", 1);

            collection.find(query).projection(projection).forEach((Consumer<Document>) doc -> {
                QuestType type = new QuestType();
                type.setId(doc.getString("questTypeId"));
                type.setName(doc.getString("questTypeName"));
                type.setSubjectId(doc.getString("subject"));
                result.add(type);
            });

            return result;
        });
    }

    public List<QuestType> getQuestTypeList(String projectId) {
        return getQuestTypeList(projectId, null);
    }


    //////////////////////////////////////////////////////////////

    public void saveQuestType(String projectId, String subjectId, String questTypeId, String questTypeName) {
        MongoCollection<Document> c = scoreDatabase.getCollection("quest_type_list");
        Document query = doc("project", projectId).append("subject", subjectId).append("questTypeId", questTypeId);
        Document update = $set(
                doc("questTypeName", questTypeName));
        UpdateResult result = c.updateMany(query, update);
        if (result.getMatchedCount() == 0) {
            c.insertOne(
                    query.append("questTypeName", questTypeName)
                            .append("md5", MD5.digest(UUID.randomUUID().toString()))
            );
        }
    }

    public void saveQuestType(Document doc) {
        saveQuestType(
                doc.getString("project"), doc.getString("subject"),
                doc.getString("questTypeId"), doc.getString("questTypeName")
        );
    }

    public void clearQuestTypes(String projectId) {
        scoreDatabase.getCollection("quest_type_list").deleteMany(doc("project", projectId));
    }
}
