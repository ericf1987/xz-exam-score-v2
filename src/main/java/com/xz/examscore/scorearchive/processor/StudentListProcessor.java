package com.xz.examscore.scorearchive.processor;

import com.hyd.simplecache.utils.MD5;
import com.mongodb.client.MongoDatabase;
import com.xz.ajiaedu.common.lang.Context;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 考试学生列表数据处理
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
        Document document = Document.parse(line).append("md5", MD5.digest(UUID.randomUUID().toString()));
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
        students.forEach(student -> student.append("md5", MD5.digest(UUID.randomUUID().toString())));
        scoreDatabase.getCollection("student_list").insertMany(students);
        context.put("students", new ArrayList<>());
    }
}
