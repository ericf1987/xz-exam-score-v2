package com.xz.examscore.scorearchive.processor;

import com.hyd.simplecache.utils.MD5;
import com.mongodb.client.MongoDatabase;
import com.xz.ajiaedu.common.lang.Context;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * 项目/科目满分数据处理
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
    protected void before(Context context) {
        scoreDatabase.getCollection("full_score").deleteMany(new Document("project", context.get("project")));
    }

    @Override
    protected void processLine(Context context, String line) {
        scoreDatabase.getCollection("full_score").insertOne(Document.parse(line.trim()).append("md5", MD5.digest(UUID.randomUUID().toString())));
    }
}
