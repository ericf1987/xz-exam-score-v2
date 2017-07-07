package com.xz.examscore.scanner;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.xz.ajiaedu.common.mongo.MongoUtils;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.services.QuestService;
import org.apache.commons.lang.BooleanUtils;
import org.bson.Document;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static com.xz.examscore.scanner.ScannerDBService.calculateScore;

/**
 * (description)
 * created at 16/06/16
 *
 * @author yiding_he
 */
public class ScannerDBServiceTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    ScannerDBService scannerDBService;

    @Autowired
    MongoClient mongoClient;

    @Autowired
    QuestService questService;

    @Test
    public void testFindProject() throws Exception {
        Document project = scannerDBService.findProject("430100-c9ccbcb7fcb542e3a2f278e8d2ca2c4f");
        System.out.println(project.toJson());
    }

    @Test
    public void test1() throws Exception {
        System.out.println(BooleanUtils.toBoolean(Boolean.parseBoolean("null")));
        System.out.println(BooleanUtils.toBoolean(true));
        System.out.println(BooleanUtils.toBoolean(false));
    }

    @Test
    public void testCalculateScore() throws Exception {
/*        assertEquals(1, calculateScore(4, "B,D", "", null).score, 0.1);
        assertEquals(0, calculateScore(4, "A,D", "DA", false).score, 0.1);
        assertEquals(1, calculateScore(4, "A", "A", null).score, 0.1);
        assertEquals(0, calculateScore(4, "A,D", "", null).score, 0.1);
        assertEquals(1, calculateScore(4, "A,D", "", true).score, 0.1);*/
        /*System.out.println(calculateScore(3, "c", "c", null).score);
        System.out.println(calculateScore(4, "A1B1DA2BC2", "DA", null).score);*/
        Document quest = questService.findQuest("430300-29c4d40d93bf41a5a82baffe7e714dd9", "58e730b52d560287557a45b8");
        String standardAnswer = scannerDBService.getStdAnswerFromQuest(quest);

//        System.out.println(scannerDBService.sortStdAnswer("BD"));
        System.out.println(calculateScore(6, standardAnswer, "ACD", false).score);
    }

    @Test
    public void testImportProject() throws Exception {
        String project = "430100-5d2142085fc747c9b5b230203bbfd402";
        scannerDBService.importProjectScore(project);
    }

    @Test
    public void testgetSubjectIdInQuestList() throws Exception {
        String project = "430100-2c641a3e36ff492aa535da7fb4cf28cf";
        String subjectId = scannerDBService.getSubjectIdInQuestList(project, "22", "007008009");
        System.out.println(subjectId);
    }

    @Test
    public void testgetStudentCardSlices() throws Exception {
        String project = "430300-c582131e66b64fe38da7d0510c399ec4";
        String subjectId = "007";
        String studentId = "000517ac-9277-4795-b0e7-0e9236b0e0b0";
        Map<String, Object> map = scannerDBService.getStudentCardSlices(project, subjectId, studentId);
        System.out.println(map.toString());
    }

    /**
     * 测试导入一个科目的分数
     * @throws Exception
     */
    @Test
    public void testImportOneSubject() throws Exception {
        String project = "431100-b332394d9bc944718c4a28778710eebc";
        MongoClient mongoClient = scannerDBService.getMongoClient(project);
        scannerDBService.importOneSubjectTask(project, mongoClient, "002");
    }

    @Test
    public void testImportStudentCardSlice() throws Exception {
        String project = "430300-9cef9f2059ce4a36a40a7a60b07c7e00";
        scannerDBService.importStudentCardSlice(project);
    }

    @Test
    public void test2() throws Exception {
        MongoClient mongoClient = scannerDBService.getMongoClient("430300-9cef9f2059ce4a36a40a7a60b07c7e00");
        String project = "430300-9cef9f2059ce4a36a40a7a60b07c7e00";
        String subjectId = "001";
        MongoCollection<Document> collection = mongoClient.getDatabase(project + "_" + subjectId).getCollection("students");
        collection.find().limit(100).forEach((Consumer<Document>) s -> System.out.println(s.getString("paper_positive")));
    }

    @Test
    public void testImportOneSubjectTask() throws Exception {
        String project = "430300-c582131e66b64fe38da7d0510c399ec4";
        MongoClient mongoClient = scannerDBService.getMongoClient(project);
        String subjectId = "003";
        scannerDBService.importOneSubjectTask(project, mongoClient, subjectId);
    }

    @Test
    public void testsortStudentAnswer() throws Exception {
        String a = scannerDBService.sortStudentAnswer("A");
        System.out.println(a);
    }

    @Test
    public void testgetStdAnswerFromQuest() throws Exception {
        String projectId = "430100-4f461da047f04f81be437e7522e68cab";
        Document quest = questService.findQuest(projectId, "005", "11");
        String stdAnswerFromQuest = scannerDBService.getStdAnswerFromQuest(quest);
        System.out.println(stdAnswerFromQuest);
    }

    @Test
    public void testisAbsent() throws Exception {
        String projectId = "431100-a827f9b3effe4081b87c8f773242c7ce";
        String subjectId = "022023";
//        String studentId = "b0ffcd4a-f881-4c9f-9762-4fc09fa1e146";
        String studentId = "66aa3961-ec4a-4d99-a151-ed4c72036f0f";
//        String studentId = "50a1ac07-fc9a-4b56-8931-bbbd2f6e0329";

        MongoClient mongoClient1 = scannerDBService.getMongoClient(projectId);

        MongoCollection<Document> students = mongoClient1.getDatabase(projectId + "_" + subjectId).getCollection("students");

        Document studentId1 = students.find(MongoUtils.doc("studentId", studentId)).first();

        scannerDBService.importStudentScore(projectId, subjectId, studentId1, new AtomicInteger(0));
/*
        boolean objectiveAllZero = scannerDBService.isObjectiveAllZero(projectId, subjectId, studentId1);

        System.out.println(scannerDBService.isAbsent(studentId1, false, objectiveAllZero));*/
    }

    @Test
    public void testimportSubjectScore0() throws Exception {
        String projectId = "431100-b332394d9bc944718c4a28778710eebc";
        String subjectId = "002";
        scannerDBService.importSubjectScore0(projectId, subjectId);
    }

    @Test
    public void testexistsSubjectDB() throws Exception {
        String projectId = "430600-d248e561aefc425b9971f2a26d267478";
        String subjectId = "007008009";
        MongoClient mongoClient = scannerDBService.getMongoClient(projectId);
        boolean b = scannerDBService.existsSubjectDB(mongoClient, projectId, subjectId);
        System.out.println(b);
    }

    @Test
    public void testisCheat() throws Exception {
        String projectId = "430600-d248e561aefc425b9971f2a26d267478";
        String subjectId = "006";
        String studentId = "1c522b51-bc79-48a3-a0bb-8a3f9517d17a";

        MongoClient mongoClient1 = scannerDBService.getMongoClient(projectId);

        MongoCollection<Document> students = mongoClient1.getDatabase(projectId + "_" + subjectId).getCollection("students");

        Document studentId1 = students.find(MongoUtils.doc("studentId", studentId)).first();

    }
}