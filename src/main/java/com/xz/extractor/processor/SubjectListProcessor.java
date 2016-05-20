package com.xz.extractor.processor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.client.MongoDatabase;
import com.xz.ajiaedu.common.lang.Context;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * ${描述}
 *
 * @author zhaorenwu
 */

@Component
public class SubjectListProcessor extends DataProcessor {

    @Autowired
    MongoDatabase scoreDatabase;

    @Override
    public String getFilePattern() {
        return "subject_list.json";
    }

    @Override
    protected void processLine(Context context, String line) {
        JSONObject jsonObject = JSON.parseObject(line);

        Document query = new Document();
        query.put("project", getString(jsonObject, "project"));

        Document document = new Document();
        List<String> subjects = getStringList(jsonObject, "subjects");
        document.put("subjects", subjects);

        Document update = new Document("$set", document);
        scoreDatabase.getCollection("subject_list").updateOne(query, update, UPSERT);
    }
}
