package com.xz.examscore.services;

import com.hyd.simplecache.utils.MD5;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;

/**
 * @author by fengye on 2017/1/7.
 */
@Service
public class ScannerDBExceptionService {
    @Autowired
    MongoDatabase scoreDatabase;

    public void recordScannerDBException(String projectId, String studentId, String subjectId, String desc){
        MongoCollection<Document> collection = scoreDatabase.getCollection("scannerDB_exception_list");
        Document query = doc("project", projectId).append("student", studentId).append("subject", subjectId).append("desc", desc)
                .append("md5", MD5.digest(UUID.randomUUID().toString()));
        collection.insertOne(query);
    }

    public void deleteRecord(String projectId, String subjectId) {
        scoreDatabase.getCollection("scannerDB_exception_list").deleteMany(doc("project", projectId).append("subject", subjectId));
    }
}
