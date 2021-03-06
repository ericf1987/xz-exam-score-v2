package com.xz.examscore.services;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;

/**
 * @author by fengye on 2016/8/10.
 */
public class ExportScoreServiceTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    ExportScoreService exportScoreService;

    @Test
    public void testExportScore() throws Exception {
/*        String filePath = "F://" + UUID.randomUUID().toString() + ".zip";

        exportScoreService.createPack("430100-3267987f5eb34ebc808581dca4c3a26d", filePath);*/
        String projectId = "431000-1a0dcbf0b1514f4f84fc1b8c7d731859";
//        exportScoreService.exportScore(projectId, true);
        String file = "F:\\1.json";
        exportScoreService.uploadPack(projectId, file);
    }

}