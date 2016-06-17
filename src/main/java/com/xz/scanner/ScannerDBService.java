package com.xz.scanner;

import com.mongodb.MongoClient;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;

/**
 * (description)
 * created at 16/06/16
 *
 * @author yiding_he
 */
@Service
public class ScannerDBService {

    @Autowired
    MongoClient scannerMongoClient;

    public Document findProject(String project) {
        return scannerMongoClient.getDatabase("project_database")
                .getCollection("project").find(doc("projectId", project)).first();
    }
}
