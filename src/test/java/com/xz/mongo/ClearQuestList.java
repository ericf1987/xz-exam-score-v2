package com.xz.mongo;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import org.bson.Document;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;

/**
 * (description)
 * created at 16/06/01
 *
 * @author yiding_he
 */
public class ClearQuestList extends XzExamScoreV2ApplicationTests {

    @Autowired
    MongoDatabase scoreDatabase;

    @Test
    public void testClearQuestList() throws Exception {

        String projectId = "430200-8a9be9fc2e1842a4b9b4894eee1f5f73";
        MongoCollection<Document> c = scoreDatabase.getCollection("quest_list");

        FindIterable<Document> documents =
                c.find(doc("project", projectId)).sort(doc("questId", 1));

        String lastQuestId = "";
        int counter = 0;
        for (Document document : documents) {
            if (document.getString("questId").equals(lastQuestId)) {
                c.deleteMany(doc("_id", document.getObjectId("_id")));
                counter++;
            } else {
                lastQuestId = document.getString("questId");
            }
        }

        System.out.println(counter + " documents removed.");
    }
}
