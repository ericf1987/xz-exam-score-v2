package com.xz.extractor.processor;

import com.mongodb.client.MongoDatabase;
import com.xz.ajiaedu.common.lang.Context;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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
    protected void before(Context context) {
        if (!context.containsKey("scores")) {
            context.put("scores", new ArrayList<>());
        }

        scoreDatabase.getCollection("score").deleteMany(new Document("project", context.get("project")));
    }

    @Override
    protected void processLine(Context context, String line) {
        Document document = Document.parse(line);
        List<Document> scores = context.get("scores");
        scores.add(document);

        // 每5000条记录提交一次
        if (scores.size() >= 5000) {
            scoreDatabase.getCollection("score").insertMany(scores);
            context.put("scores", new ArrayList<>());
        }
    }

    @Override
    protected void after(Context context) {
        List<Document> scores = context.get("scores");
        scoreDatabase.getCollection("score").insertMany(scores);
        context.put("scores", new ArrayList<>());
    }
}
