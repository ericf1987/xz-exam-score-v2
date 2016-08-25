package com.xz.examscore.services;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import org.bson.Document;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

import static com.xz.ajiaedu.common.mongo.MongoUtils.*;

/**
 * @author by fengye on 2016/7/28.
 */
public class SchoolServiceTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    SchoolService schoolService;

    @Autowired
    MongoDatabase scoreDatabase;

    public void paddingTags2(String projectId) {
        MongoCollection<Document> c = scoreDatabase.getCollection("school_list");

        c.find(doc("project", projectId)).forEach((Consumer<Document>) document -> {
            c.updateOne(doc("project", projectId).append("school", document.getString("school")),
                    $set("tags", getRandomValue()),
                    UPSERT);
        });
    }

    private List<String> getRandomValue() {
        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");

        Random r = new Random();
        if (r.nextInt(10) > 7) {
            list.remove(1);
            return list;
        } else if (r.nextInt(10) > 3) {
            list.remove(2);
            return list;
        } else {
            list.remove(3);
            return list;
        }
    }

    @Test
    public void testGetSchoolsByTags() throws Exception {
        /*List<Document> doc = schoolService.getSchoolsByTags("430300-672a0ed23d9148e5a2a31c8bf1e08e62", "false", "true");
        System.out.println(doc.toString());*/
        paddingTags2("430300-672a0ed23d9148e5a2a31c8bf1e08e62");
    }

    @Test
    public void testGetSchoolsByTags1() throws Exception {
        String projectId = "430300-672a0ed23d9148e5a2a31c8bf1e08e62";
        List<String> query = Arrays.asList("1", "3");
        List<Document> schools = schoolService.getSchoolsByTags(projectId, query);
        schools.forEach(System.out::println);
    }
}