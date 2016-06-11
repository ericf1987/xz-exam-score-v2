package com.xz.services;

import com.hyd.simplecache.SimpleCache;
import com.mongodb.client.MongoDatabase;
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
     * 查询指定的项目中有哪些题型
     *
     * @param projectId 项目ID
     *
     * @return 题型列表
     */
    public List<String> getQuestTypeList(String projectId) {
        String cacheKey = "quest_type_list:" + projectId;

        return cache.get(cacheKey, () -> {
            ArrayList<String> result = new ArrayList<>();
            Document query = doc("project", projectId).append("questionTypeId", $ne(null));

            scoreDatabase.getCollection("quest_list")
                    .distinct("questionTypeId", query, String.class)
                    .forEach((Consumer<String>) result::add);

            return result;
        });
    }
}
