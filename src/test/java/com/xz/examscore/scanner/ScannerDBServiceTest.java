package com.xz.examscore.scanner;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import org.bson.Document;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

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

    @Test
    public void testFindProject() throws Exception {
        Document project = scannerDBService.findProject(SCANNER_PROJECT_ID);
        System.out.println(project.toJson());
    }

    @Test
    public void testCalculateScore() throws Exception {
        assertEquals(1, calculateScore(1, "A,D", "AD", null).score, 0.1);
        assertEquals(0, calculateScore(1, "A,D", "AD", false).score, 0.1);
        assertEquals(1, calculateScore(1, "A", "A", null).score, 0.1);
        assertEquals(0, calculateScore(1, "A,D", "", null).score, 0.1);
        assertEquals(1, calculateScore(1, "A,D", "", true).score, 0.1);
    }

    @Test
    public void testImport() throws Exception {
        String project = "430200-b73f03af1d74484f84f1aa93f583caaa";
        scannerDBService.importSubjectScore(project, "004");
    }

    @Test
    public void testImportProject() throws Exception {
        String project = "430100-2c641a3e36ff492aa535da7fb4cf28cf";
        scannerDBService.importProjectScore(project);
    }
}