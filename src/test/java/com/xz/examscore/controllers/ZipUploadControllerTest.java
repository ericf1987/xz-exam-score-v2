package com.xz.examscore.controllers;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.xz.ajiaedu.common.mongo.MongoUtils;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.ScoreLevelService;
import com.xz.examscore.util.Mongo;
import org.bson.Document;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.xz.ajiaedu.common.mongo.MongoUtils.*;

/**
 * @author by fengye on 2016/7/13.
 */
@SuppressWarnings("unchecked")
public class ZipUploadControllerTest extends XzExamScoreV2ApplicationTests {
    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    ScoreLevelService scoreLevelService;

    public static final String PROJECTID = "430200-b73f03af1d74484f84f1aa93f583caaa";

    public static final String SCHOOLID = "200f3928-a8bd-48c4-a2f4-322e9ffe3700";

    public static final String CLASSID = "045f40c8-cf84-4e2e-a10c-2f33c74c1336";

    public static final String STUDENTID = "001e345d-a9a0-480e-8a7b-155a6954144d";

    public static final String QUEST = "5771e4c62d560287556e7219";

    @Test
    public void testArea_list() {
        MongoCollection collection = scoreDatabase.getCollection("area_list");
        Document query = new Document().append("project", PROJECTID);
        FindIterable<Document> area_list = collection.find(query);
        area_list.forEach((Consumer<Document>) document -> System.out.println(document.toString()));

    }

    @Test
    public void testAbility_list() {
        MongoCollection collection = scoreDatabase.getCollection("ability_levels");
        Document query = MongoUtils.doc().append("study_stage", "1").append("ability_type", "0");
        FindIterable<Document> ability_levels = collection.find(query);
        ability_levels.forEach((Consumer<Document>) document -> System.out.println(document.toString()));

        MongoCursor cursor = ability_levels
                .sort(doc("study_stage", 1))
                .projection(WITHOUT_INNER_ID).iterator();
        List<Document> l = new ArrayList<>();
        while (cursor.hasNext()) {
            Document d = (Document) cursor.next();
            l.add(d);
        }

        System.out.println("转化后的document-->" + l.toString());
    }

    @Test
    public void testCities() {
        MongoCollection collection = scoreDatabase.getCollection("cities");
        Document query = doc("parent_id", "000000");
        FindIterable<Document> cities = collection.find(query);
        List<Document> cityIds = toList(cities).stream()
                .map(city -> doc("cityId", city.getString("id")))
                .collect(Collectors.toList());
        System.out.println("cities表中的所有Id-->" + cityIds.toString());
    }

    @Test
    public void testScore() {
        List<Document> list = new ArrayList<>();
//        Document query = Mongo.query(PROJECTID, Range.student(STUDENTID), Target.quest(QUEST));
//        Document query1 = doc("project", PROJECTID).append("student", STUDENTID).append("quest", QUEST);
        //Document query = doc("project", PROJECTID).append("range.id", SCHOOLID).append("target.id", QUEST);
        Document query = doc("project", PROJECTID)
                .append("range", Mongo.range2Doc(Range.school(SCHOOLID)))
                .append("target", Mongo.target2Doc(Target.quest(QUEST)));
//        list.addAll(toList(scoreDatabase.getCollection("score").find(query).projection(doc("totalScore", 1)).limit(5)));
        list.addAll(toList(scoreDatabase.getCollection("total_score").find(query).projection(doc("totalScore", 1)).limit(5)));
        System.out.println(list.toString());
    }

    public Document range2Doc(Range range){
        return new Document().append("name", range.getName()).append("id", range.getId());
    }

    public Range doc2Range(Document document){
        return new Range(document.getString("name"), document.getString("id"));
    }

    @Test
    public void testScoreLevel(){
        double score_level = 0.81d;
        Document query = doc("projectId", PROJECTID);
        Document project_config = scoreDatabase.getCollection("project_config").find().first();
        Map<String, Object> scoreLevels = (Map<String, Object>)project_config.get("scoreLevels");
    }

    @Test
    public void testScoreLevel2(){
        System.out.println(scoreLevelService.calculateScoreLevel(PROJECTID, 0.81d));
    }

/*    public static void main(String[] args) {
        File parent = new File("F:\\test\\file");
        File file = new File(parent, "test1.txt");
        try{
            parent.mkdirs();
            System.out.println(file.createNewFile());
        }catch(Exception e){
            e.printStackTrace();
        }
    }*/
}