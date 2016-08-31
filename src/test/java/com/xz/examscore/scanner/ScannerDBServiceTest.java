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
        boolean awardScoreTag = false;
        assertEquals(1, calculateScore(1, "A,D", "AD", awardScoreTag).score, 0.1);
        assertEquals(1, calculateScore(1, "A,D", "DA", awardScoreTag).score, 0.1);
        assertEquals(0, calculateScore(1, "A,D", "", awardScoreTag).score, 0.1);
    }

    @Test
    public void testImport() throws Exception {
        String project = "430200-b73f03af1d74484f84f1aa93f583caaa";
        scannerDBService.importSubjectScore(project, "004");
    }
}