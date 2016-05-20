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
public class ClassListProcessor extends DataProcessor {

    @Autowired
    MongoDatabase scoreDatabase;

    @Override
    public String getFilePattern() {
        return "class_list.json";
    }

    @Override
    protected void processLine(Context context, String line) {
        JSONObject jsonObject = JSON.parseObject(line);

        Document query = new Document();
        query.put("project", getString(jsonObject, "project"));
        query.put("school", getString(jsonObject, "school"));
        query.put("class", getString(jsonObject, "class"));

        Document document = new Document();
        document.put("name", getString(jsonObject, "name"));
        document.put("grade", getString(jsonObject, "grade"));
        document.put("province", getString(jsonObject, "province"));
        document.put("city", getString(jsonObject, "city"));
        document.put("area", getString(jsonObject, "area"));

        Document update = new Document("$set", document);
        scoreDatabase.getCollection("class_list").updateOne(query, update, UPSERT);
    }
}
