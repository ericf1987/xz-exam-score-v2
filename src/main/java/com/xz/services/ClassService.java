package com.xz.services;

import com.hyd.simplecache.SimpleCache;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.ajiaedu.common.mongo.MongoUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.xz.ajiaedu.common.mongo.DocumentUtils.addTo;
import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;
import static com.xz.ajiaedu.common.mongo.MongoUtils.toList;

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

    /**
     * 查询班级列表
     *
     * @param projectId 考试项目id
     * @param schoolId  学校id
     *
     * @return  班级列表
     */
    public List<Document> listClasses(String projectId, String schoolId) {
        String cacheKey = "class_list:" + projectId + ":" + schoolId;

        return cache.get(cacheKey, () -> {
            ArrayList<Document> classes = new ArrayList<>();

            MongoCollection<Document> collection = scoreDatabase.getCollection("class_list");
            Document query = doc("project", projectId);
            addTo(query, "school", schoolId);

            classes.addAll(toList(collection.find(query).projection(MongoUtils.WITHOUT_INNER_ID)));
            return classes;
        });
    }
}
