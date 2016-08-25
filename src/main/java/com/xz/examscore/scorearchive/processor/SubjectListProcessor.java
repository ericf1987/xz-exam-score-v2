package com.xz.examscore.scorearchive.processor;

import com.mongodb.client.MongoDatabase;
import com.xz.ajiaedu.common.lang.Context;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 考试科目列表数据处理
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
    protected void before(Context context) {
        scoreDatabase.getCollection("subject_list").deleteMany(new Document("project", context.get("project")));
    }

    @Override
    protected void processLine(Context context, String line) {
        scoreDatabase.getCollection("subject_list").insertOne(Document.parse(line));
    }
}
