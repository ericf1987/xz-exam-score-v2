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
public class ScoreProcessor extends DataProcessor {

    @Autowired
    MongoDatabase scoreDatabase;

    @Override
    public String getFilePattern() {
        return "score.json";
    }

    @Override
    protected void processLine(Context context, String line) {
        JSONObject jsonObject = JSON.parseObject(line);

        Document document = new Document();
        document.put("project", getString(jsonObject, "project"));
        document.put("student", getString(jsonObject, "student"));
        document.put("subject", getString(jsonObject, "subject"));
        document.put("quest", getString(jsonObject, "quest"));
        document.put("questNo", getString(jsonObject, "questNo"));
        document.put("isObjective", getString(jsonObject, "isObjective"));
        document.put("score", getString(jsonObject, "score"));
        document.put("right", getString(jsonObject, "right"));
        document.put("answer", getString(jsonObject, "answer", ""));
        document.put("class", getString(jsonObject, "class"));
        document.put("school", getString(jsonObject, "school"));
        document.put("area", getString(jsonObject, "area"));
        document.put("city", getString(jsonObject, "city"));
        document.put("province", getString(jsonObject, "province"));
        scoreDatabase.getCollection("score").insertOne(document);
    }
}
