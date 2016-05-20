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
public class SchoolListProcessor extends DataProcessor {

    @Autowired
    MongoDatabase scoreDatabase;

    @Override
    public String getFilePattern() {
        return "school_list.json";
    }

    @Override
    protected void processLine(Context context, String line) {
        JSONObject jsonObject = JSON.parseObject(line);

        Document query = new Document();
        query.put("project", getString(jsonObject, "project"));
        query.put("school", getString(jsonObject, "school"));

        Document document = new Document();
        document.put("name", getString(jsonObject, "name"));
        document.put("area", getString(jsonObject, "area"));
        document.put("city", getString(jsonObject, "city"));
        document.put("province", getString(jsonObject, "province"));

        Document update = new Document("$set", document);
        scoreDatabase.getCollection("school_list").updateOne(query, update, UPSERT);
    }
}
