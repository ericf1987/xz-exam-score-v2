package com.xz.examscore.scanner;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.services.QuestService;
import org.apache.commons.lang.BooleanUtils;
import org.bson.Document;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
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
        System.out.println(scannerDBService.sortStdAnswer("BD"));
        System.out.println(calculateScore(4, "B, D", " ", false).score);
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
        String project = "430300-9cef9f2059ce4a36a40a7a60b07c7e00";
        String subjectId = "001";
        String studentId = "599e38a8-41af-40e8-be01-23c2b9898fcd";
        Map<String, Object> map = scannerDBService.getStudentCardSlices(project, subjectId, studentId);
        System.out.println(map.toString());
    }

    @Test
    public void testImportOneSubject() throws Exception {
        String project = "430300-9cef9f2059ce4a36a40a7a60b07c7e00";
        MongoClient mongoClient = scannerDBService.getMongoClient(project);
        scannerDBService.importOneSubjectTask(project, mongoClient, "001");
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
        String project = "430500-858a2da0e24f4c329aafb9071e022e3b";
        MongoClient mongoClient = scannerDBService.getMongoClient("430500-858a2da0e24f4c329aafb9071e022e3b");
        String subjectId = "001";
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
}