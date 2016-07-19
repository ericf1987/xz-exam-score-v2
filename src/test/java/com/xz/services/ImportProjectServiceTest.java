package com.xz.services;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.ajiaedu.common.lang.Context;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * (description)
 * created at 16/07/08
 *
 * @author yiding_he
 */
public class ImportProjectServiceTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    ImportProjectService importProjectService;

    @Test
    public void testImportProjectQuest() throws Exception {
        importProjectService.importProject("430200-e1274973fe994a86a9552a168fdeaa01", false);
    }

    @Test
    public void testImportReportConfig() throws Exception {
        importProjectService.importProjectReportConfig("430100-34e1ab4e8749435ebb798b3779305839", new Context());
    }
}