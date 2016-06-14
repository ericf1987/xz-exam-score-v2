package com.xz.controllers;

import com.xz.XzExamScoreV2ApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

/**
 * (description)
 * created at 16/06/14
 *
 * @author yiding_he
 */
public class ScoreExportControllerTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    ScoreExportController scoreExportController;

    @Test
    public void testCreatePack() throws Exception {
        scoreExportController.createPack(PROJECT_ID, "score-archives/" + UUID.randomUUID().toString() + ".zip");
    }
}