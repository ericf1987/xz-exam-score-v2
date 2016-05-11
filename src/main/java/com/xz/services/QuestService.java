package com.xz.services;

import com.hyd.simplecache.SimpleCache;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public Document findQuest(String id) {
        String cacheKey = "quest:" + id;

        return simpleCache.get(cacheKey, () ->
                scoreDatabase.getCollection("quest_list")
                        .find(new Document("_id", new ObjectId(id))).first());
    }
}
