package com.xz.examscore.controllers;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;

/**
 * (description)
 * created at 16/06/15
 *
 * @author yiding_he
 */
public class ZipScoreUploadControllerTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    ZipScoreUploadController zipScoreUploadController;

    @Test
    public void testImportScoreFile() throws Exception {
        String project = "430500-6539f2f49f74411a8a1beb232a0cedf1";
        zipScoreUploadController.importScoreFile(project, new File("target/score.zip"));


    }
}