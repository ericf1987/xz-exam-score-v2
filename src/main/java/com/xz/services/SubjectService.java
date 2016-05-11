package com.xz.services;

import com.hyd.simplecache.SimpleCache;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;

/**
 * (description)
 * created at 16/05/11
 *
 * @author yiding_he
 */
@Service
public class SubjectService {

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    SimpleCache simpleCache;

    @SuppressWarnings("unchecked")
    public List<String> querySubjects(String projectId) {
        String cacheKey = "subject_list:" + projectId;

        return simpleCache.get(cacheKey, () -> {
            ArrayList<String> targets = new ArrayList<>();
            MongoCollection<Document> collection = scoreDatabase.getCollection("subject_list");

            collection.find(doc("project", projectId)).forEach((Consumer<Document>) document -> {
                List<String> subjectIds = (List<String>) document.get("subjects");
                targets.addAll(subjectIds);
            });

            return targets;
        });
    }
}
