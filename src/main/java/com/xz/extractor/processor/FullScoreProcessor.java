package com.xz.extractor.processor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.client.MongoDatabase;
import com.xz.ajiaedu.common.lang.Context;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * ${描述}
 *
 * @author zhaorenwu
 */

@Component
public class FullScoreProcessor extends DataProcessor {

    @Autowired
    MongoDatabase scoreDatabase;

    @Override
    public String getFilePattern() {
        return "full_score.json";
    }

    @Override
    protected void processLine(Context context, String line) {
        JSONObject jsonObject = JSON.parseObject(line);
        JSONObject targetObj = getJsonObject(jsonObject, "target");

        Document query = new Document();
        query.put("project", getString(jsonObject, "project"));
        query.put("target.id", getString(targetObj, "id"));

        Document document = new Document();
        Document target = new Document();
        target.put("name", getString(targetObj, "name"));
        target.put("id", getString(targetObj, "id"));
        document.put("target", target);
        document.put("fullScore", getString(jsonObject, "fullScore"));

        Document update = new Document("$set", document);
        scoreDatabase.getCollection("full_score").updateOne(query, update, UPSERT);
    }
}
