package com.xz.mongo;

import com.mongodb.client.MongoDatabase;
import com.xz.XzExamScoreV2ApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
}
