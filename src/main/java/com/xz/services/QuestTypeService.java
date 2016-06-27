package com.xz.services;

import com.hyd.simplecache.SimpleCache;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.ajiaedu.common.beans.dic.QuestType;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.xz.ajiaedu.common.mongo.MongoUtils.$ne;
import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;

@Service
public class QuestTypeService {

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    SimpleCache cache;

    /**
     * 查询指定的项目中有哪些题型（项目初始化用，从 quest_list 中查询，其他方法从 quest_type_list 中查询）
     *
     * @param projectId 项目ID
     *
     * @return 题型列表
     */
    public List<QuestType> getQuestTypeList(String projectId) {
        String cacheKey = "quest_type_list:" + projectId;

        return cache.get(cacheKey, () -> {
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
     *
     * @return 题型信息
     */
    public QuestType getQuestType(String projectId, String questTypeId) {
        String cacheKey = "quest_type:" + projectId + ":" + questTypeId;

        return cache.get(cacheKey, () -> {
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
     * @param subjectId 科目ID
     *
     * @return 题型列表
     */
    public List<QuestType> getQuestTypeList(String projectId, String subjectId) {
        String cacheKey = "subject_quest_type:" + projectId + ":" + subjectId;
        return cache.get(cacheKey, () -> {
            ArrayList<QuestType> result = new ArrayList<QuestType>();
            MongoCollection<Document> collection = scoreDatabase.getCollection("quest_type_list");

            Document query = doc("project", projectId).append("subject", subjectId);
            Document projection = doc("questTypeId", 1).append("questTypeName", 1);

            collection.find(query).projection(projection).forEach((Consumer<Document>) doc -> {
                QuestType type = new QuestType();
                type.setId(doc.getString("questTypeId"));
                type.setName(doc.getString("questTypeName"));
                type.setSubjectId(subjectId);
                result.add(type);
            });

            return result;
        });
    }
}
