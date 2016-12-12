package com.xz.examscore.scanner;

import com.mongodb.MongoClient;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import org.bson.Document;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static com.xz.examscore.scanner.ScannerDBService.calculateScore;
import static org.junit.Assert.assertEquals;

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

    @Test
    public void testFindProject() throws Exception {
        Document project = scannerDBService.findProject("430100-c9ccbcb7fcb542e3a2f278e8d2ca2c4f");
        System.out.println(project.toJson());
    }

    @Test
    public void testCalculateScore() throws Exception {
/*        assertEquals(1, calculateScore(4, "B,D", "", null).score, 0.1);
        assertEquals(0, calculateScore(4, "A,D", "AD", false).score, 0.1);
        assertEquals(1, calculateScore(4, "A", "A", null).score, 0.1);
        assertEquals(0, calculateScore(4, "A,D", "", null).score, 0.1);
        assertEquals(1, calculateScore(4, "A,D", "", true).score, 0.1);*/
        System.out.println(calculateScore(3, "C", "", null).score);
    }

    @Test
    public void testImportProject() throws Exception {
        String project = "433100-fef19389d6ce4b1f99847ab96d2cfeba";
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
        String project = "430100-f00975f88b4e4881925613b2a238673f";
        String subjectId = "002";
        String studentId = "9796e596-ac91-4114-bc37-f494cedc9271";
        Map<String, Object> map = scannerDBService.getStudentCardSlices(project, subjectId, studentId);
        System.out.println(map.toString());
    }

}