package com.xz.examscore.scanner;

import com.mongodb.MongoClient;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import org.apache.commons.lang.BooleanUtils;
import org.bson.Document;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.Arrays;
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
        System.out.println(scannerDBService.sortStdAnswer("A2C2D2AC2AD2CD2DCA4"));
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
        String project = "430400-20bdc0221ebc46ffa642fa316721a92e";
        String subjectId = "006";
        String studentId = "925bb587-16b0-41a0-8fa5-9ad0b0e7cdae";
        Map<String, Object> map = scannerDBService.getStudentCardSlices(project, subjectId, studentId);
        System.out.println(map.toString());
    }

}