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
public class StudentListProcessor extends DataProcessor {

    @Autowired
    MongoDatabase scoreDatabase;

    @Override
    public String getFilePattern() {
        return "student_list.json";
    }

    @Override
    protected void before(Context context) {
        if (!context.containsKey("students")) {
            context.put("students", new ArrayList<>());
        }

        scoreDatabase.getCollection("student_list").deleteMany(new Document("project", context.get("project")));
    }

    @Override
    protected void processLine(Context context, String line) {
        Document document = Document.parse(line);
        List<Document> students = context.get("students");
        students.add(document);

        // 每2000条记录提交一次
        if (students.size() >= 2000) {
            scoreDatabase.getCollection("student_list").insertMany(students);
            context.put("students", new ArrayList<>());
        }
    }

    @Override
    protected void after(Context context) {
        List<Document> students = context.get("students");
        scoreDatabase.getCollection("student_list").insertMany(students);
        context.put("students", new ArrayList<>());
    }
}
