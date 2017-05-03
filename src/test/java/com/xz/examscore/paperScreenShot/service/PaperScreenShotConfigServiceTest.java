package com.xz.examscore.paperScreenShot.service;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import org.bson.Document;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * @author by fengye on 2017/5/3.
 */
public class PaperScreenShotConfigServiceTest extends XzExamScoreV2ApplicationTests {

    public static final String PROJECT_ID = "430900-8f11fe8dbac842a3805d45e05eb31095";

    @Autowired
    PaperScreenShotConfigService paperScreenShotConfigService;

    @Test
    public void testGetConfigFromCMS() throws Exception {
        Map<String, Object> configFromCMS = paperScreenShotConfigService.getConfigFromCMS(PROJECT_ID);
        System.out.println(configFromCMS.toString());
    }

    @Test
    public void testPssConfig2Doc() throws Exception {
        Document document = paperScreenShotConfigService.pssConfig2Doc(PROJECT_ID);
        paperScreenShotConfigService.saveConfig(PROJECT_ID, document);
    }

    @Test
    public void testSaveConfig() throws Exception {

    }

    @Test
    public void testRemoveConfig() throws Exception {

    }
}