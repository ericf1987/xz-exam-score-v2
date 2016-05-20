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
public class QuestListProcessor extends DataProcessor {

    @Autowired
    MongoDatabase scoreDatabase;

    @Override
    public String getFilePattern() {
        return "quest_list.json";
    }

    @Override
    protected void processLine(Context context, String line) {
        JSONObject jsonObject = JSON.parseObject(line);

        Document query = new Document();
        query.put("project", getString(jsonObject, "project"));
        query.put("questId", getString(jsonObject, "questId"));

        Document document = new Document();
        document.put("subject", getString(jsonObject, "subject"));
        document.put("isObjective", getString(jsonObject, "isObjective", ""));
        document.put("questNo", getString(jsonObject, "questNo"));
        document.put("score", getString(jsonObject, "score"));
        document.put("standardAnswer", getString(jsonObject, "standardAnswer", ""));

        Document update = new Document("$set", document);
        scoreDatabase.getCollection("quest_list").updateOne(query, update, UPSERT);
    }
}
