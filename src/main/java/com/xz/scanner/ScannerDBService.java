package com.xz.scanner;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

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

    public void importSubjectScore(String project, String subjectId) {
        String dbName = project + "_" + subjectId;
        MongoCollection<Document> collection = scannerMongoClient.getDatabase(dbName).getCollection("students");

        collection.find(doc()).forEach(
                (Consumer<Document>) doc -> importStudentScore(project, subjectId, doc));
    }

    private void importStudentScore(String projectId, String subjectId, Document document) {
        //
    }
}
