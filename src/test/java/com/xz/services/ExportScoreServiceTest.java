package com.xz.services;

import com.xz.XzExamScoreV2ApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/8/10.
 */
public class ExportScoreServiceTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    ExportScoreService exportScoreService;

    @Test
    public void testExportScore() throws Exception {
        String filePath = "F://" + UUID.randomUUID().toString() + ".zip";

        exportScoreService.createPack("430100-3267987f5eb34ebc808581dca4c3a26d", filePath);
    }

    @Test
    public void testCreatePack() throws Exception {

    }
}