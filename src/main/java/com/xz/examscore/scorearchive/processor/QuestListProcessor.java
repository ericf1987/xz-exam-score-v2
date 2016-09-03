package com.xz.examscore.scorearchive.processor;

import com.hyd.simplecache.utils.MD5;
import com.mongodb.client.MongoDatabase;
import com.xz.ajiaedu.common.lang.Context;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * 考试题列表数据处理
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
    protected void before(Context context) {
        scoreDatabase.getCollection("quest_list").deleteMany(new Document("project", context.get("project")));
    }

    @Override
    protected void processLine(Context context, String line) {
        scoreDatabase.getCollection("quest_list").insertOne(Document.parse(line.trim()).append("md5", MD5.digest(UUID.randomUUID().toString())));
    }
}
