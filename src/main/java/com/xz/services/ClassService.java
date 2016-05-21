package com.xz.services;

import com.hyd.simplecache.SimpleCache;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;

/**
 * (description)
 * created at 16/05/21
 *
 * @author yiding_he
 */
@Service
public class ClassService {

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    SimpleCache cache;

    public Document findClass(String projectId, String classId) {
        String cacheKey = "class:" + projectId + ":" + classId;

        return cache.get(cacheKey, () -> {
            MongoCollection<Document> collection = scoreDatabase.getCollection("class_list");
            Document query = doc("project", projectId).append("class", classId);
            return collection.find(query).first();
        });
    }
}
