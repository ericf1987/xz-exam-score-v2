package com.xz.services;

import com.hyd.simplecache.SimpleCache;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;
import static com.xz.ajiaedu.common.mongo.MongoUtils.toList;

/**
 * (description)
 * created at 16/05/24
 *
 * @author yiding_he
 */
@Service
public class SchoolService {

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    SimpleCache cache;

    public List<Document> getProjectSchools(String projectId) {
        String cacheKey = "school_list:" + projectId;

        return cache.get(cacheKey, () -> {
            ArrayList<Document> result = new ArrayList<>();
            result.addAll(toList(scoreDatabase.getCollection("school_list").find(doc("project", projectId))));
            return result;
        });
    }
}
