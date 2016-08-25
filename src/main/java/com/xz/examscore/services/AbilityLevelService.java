package com.xz.examscore.services;

import com.hyd.simplecache.SimpleCache;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.ajiaedu.common.lang.CollectionUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;
import static com.xz.ajiaedu.common.mongo.MongoUtils.toList;

/**
 * 能力层级字典信息
 *
 * @author zhaorenwu
 */
@Service
public class AbilityLevelService {

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    SimpleCache cache;

    /**
     * 查询能力层级名称
     *
     * @param studyStage    学段
     * @param subjectId     科目id
     * @param levelId       能力层级id
     *
     * @return  能力层级名称
     */
    public String queryAbilityLevelName(String studyStage, String subjectId, String levelId) {
        Map<String, Document> levelMap = queryAbilityLevels(studyStage, subjectId);
        Document levelInfo = levelMap.get(levelId);

        if (levelInfo == null) {
            return "";
        } else {
            return levelInfo.getString("level_name");
        }
    }

    /**
     * 查询科目的能力层级定义
     *
     * @param studyStage  学段
     * @param subjectId   科目ID
     *
     * @return 能力层级信息
     */
    public Map<String, Document> queryAbilityLevels(String studyStage, String subjectId) {
        String cacheKey = "ability_levels:" + studyStage + ":" + subjectId;
        return cache.get(cacheKey, () -> {
            MongoCollection<Document> collection = scoreDatabase.getCollection("ability_levels");
            Document query = doc("study_stage", studyStage).append("subject", subjectId);

            return new HashMap<>(CollectionUtils.toMap(toList(collection.find(query)),
                    document -> document.getString("level_id")));
        });
    }
}
