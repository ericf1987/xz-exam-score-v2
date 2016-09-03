package com.xz.examscore.services;

import com.hyd.simplecache.utils.MD5;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.ajiaedu.common.mongo.MongoUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * (description)
 * created at 16/06/05
 *
 * @author yiding_he
 */
@Service
public class AreaService {

    @Autowired
    MongoDatabase scoreDatabase;

    public void saveProjectAreas(String projectId, Collection<String> areas) {

        if (areas.isEmpty()) {
            return;
        }

        List<Document> documents = areas.stream()
                .map(area -> MongoUtils.doc("project", projectId).append("area", area).append("md5", MD5.digest(UUID.randomUUID().toString())))
                .collect(Collectors.toList());

        MongoCollection<Document> collection = scoreDatabase.getCollection("area_list");
        collection.deleteMany(MongoUtils.doc("project", projectId));
        collection.insertMany(documents);
    }
}
