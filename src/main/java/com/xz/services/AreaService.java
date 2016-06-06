package com.xz.services;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.ajiaedu.common.mongo.MongoUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
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

        List<Document> documents = areas.stream()
                .map(area -> MongoUtils.doc("project", projectId).append("area", area))
                .collect(Collectors.toList());

        MongoCollection<Document> collection = scoreDatabase.getCollection("area_list");
        collection.deleteMany(MongoUtils.doc("project", projectId));
        collection.insertMany(documents);
    }
}
