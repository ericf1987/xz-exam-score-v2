package com.xz.examscore.services;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.util.Mongo;
import org.bson.Document;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * (description)
 * created at 16/06/01
 *
 * @author yiding_he
 */
public class DeleteRecordTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    MongoDatabase scoreDatabase;

    @Test
    public void testDeleteRecord() throws Exception {
        String projectId = "430200-89c9dc7481cd47a69d85af3f0808e0c4";
        Range range = Range.school("7e34fa5e-9023-4ad4-b4fa-fe4e3d7d1b52");
        Target target = Target.quest("573c49e62d560287556b8a76");

        MongoCollection<Document> c = scoreDatabase.getCollection("score_map");
        DeleteResult deleteResult = c.deleteMany(Mongo.query(projectId, range, target));
        System.out.println(deleteResult.getDeletedCount());
    }
}
