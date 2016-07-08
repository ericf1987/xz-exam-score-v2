package com.xz.services;

import com.xz.XzExamScoreV2ApplicationTests;
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
}