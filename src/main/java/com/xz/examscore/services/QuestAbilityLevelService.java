package com.xz.examscore.services;

import com.hyd.simplecache.SimpleCache;
import com.hyd.simplecache.utils.MD5;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import com.xz.ajiaedu.common.lang.CollectionUtils;
import com.xz.ajiaedu.common.lang.StringUtil;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static com.xz.ajiaedu.common.mongo.MongoUtils.*;

/**
 * @author by fengye on 2017/1/3.
 */
@Service
public class QuestAbilityLevelService {

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    SimpleCache cache;

    public void clearQuestAbilityLevel(String projectId) {
        scoreDatabase.getCollection("quest_ability_level_list").deleteMany(doc("project", projectId));
    }

    public void saveQuestAbilityLevel(Document doc) {
        saveQuestAbilityLevel(doc.getString("project"), doc.getString("subject"), doc.getString("levelOrAbility"), doc.getString("questAbilityLevel"), doc.getString("questAbilityLevelName"));
    }

    private void saveQuestAbilityLevel(String project, String subject, String levelOrAbility, String questAbilityLevel, String name) {
        MongoCollection<Document> collection = scoreDatabase.getCollection("quest_ability_level_list");
        Document query = doc("project", project).append("subject", subject).append("levelOrAbility", levelOrAbility).append("questAbilityLevel", questAbilityLevel);
        Document update = $set(
                doc("questAbilityLevelName", name));
        UpdateResult result = collection.updateMany(query, update);
        if (result.getMatchedCount() == 0) {
            collection.insertOne(
                    query.append("questAbilityLevelName", name)
                            .append("md5", MD5.digest(UUID.randomUUID().toString()))
            );
        }
    }

    public List<String> queryQuestAbilityLevels(String projectId) {
        String cacheKey = "quest_ability_level_list:" + projectId;

        return cache.get(cacheKey, () -> {
            List<String> result = new ArrayList<>();
            MongoCollection<Document> collection = scoreDatabase.getCollection("quest_ability_level_list");
            Document query = doc("project", projectId).append("questAbilityLevel", $ne(null));

            Consumer<String> getQuestType = quest_ability_level -> {
                Document q = doc("project", projectId).append("questAbilityLevel", quest_ability_level);
                Document p = doc("questAbilityLevelName", 1).append("subject", 1).append("levelOrAbility", 1);
                Document d = collection.find(q).projection(p).first();

                if (d != null) {
                    result.add(d.getString("questAbilityLevel"));
                }
            };

            collection.distinct("questAbilityLevel", query, String.class).forEach(getQuestType);
            return CollectionUtils.asArrayList(result);
        });
    }

    public Document getQuestAbilityLevelDoc(String projectId, String questAbilityLevel, String subjectId, String levelOrAbility){
        Document query = getQuery(projectId, questAbilityLevel, subjectId, levelOrAbility);
        FindIterable<Document> documents = scoreDatabase.getCollection("quest_ability_level_list").find(query);
        return null != documents ? documents.first() : null;
    }

    public String getId(String subjectId, String abilityLevel){
        if(!StringUtil.isBlank(abilityLevel)){
            return subjectId + "_" + abilityLevel;
        }
        return null;
    }

    public String getSubjectId(String questAbilityLevel){
        String[] arr = questAbilityLevel.split("_");
        return arr[0];
    }

    public String getLevel(String questAbilityLevel){
        String[] arr = questAbilityLevel.split("_");
        return arr[1];
    }

    public Document getQuery(String projectId, String questAbilityLevel, String subjectId, String levelOrAbility) {
        Document q = doc("project", projectId).append("questAbilityLevel", questAbilityLevel);
        if (!StringUtil.isBlank(subjectId))
            q.append("subject", subjectId);
        if (!StringUtil.isBlank(levelOrAbility))
            q.append("levelOrAbility", levelOrAbility);
        return q;
    }

}
