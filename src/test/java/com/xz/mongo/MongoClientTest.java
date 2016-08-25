package com.xz.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import org.bson.Document;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;

/**
 * (description)
 * created at 16/05/30
 *
 * @author yiding_he
 */
public class MongoClientTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    MongoDatabase scoreDatabase;

    @Test
    public void testCreateIndex() throws Exception {

    }

    @Test
    public void testQuery() throws Exception {
        MongoCollection<Document> c = scoreDatabase.getCollection("top_student_list");
        Document query = doc("school", "c36c331a-3625-4268-9e6e-b92f3ba7aacc");
        Document sort = doc("rank", -1);

        c.find(query).sort(sort).first();
    }
}
