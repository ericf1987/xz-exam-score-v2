package com.xz.scanner;

import com.xz.XzExamScoreV2ApplicationTests;
import org.bson.Document;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
}