package com.xz.examscore.paperScreenShot.service;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2017/3/1.
 */
public class PaperScreenShotServiceTest extends XzExamScoreV2ApplicationTests{

    @Autowired
    PaperScreenShotService paperScreenShotService;

    @Test
    public void testStartPaperScreenShotTask() throws Exception {
        String projectId = "430300-9cef9f2059ce4a36a40a7a60b07c7e00";
        paperScreenShotService.startPaperScreenShotTask(projectId);
    }

    @Test
    public void testStartTask() throws Exception {
        String projectId = "430300-9cef9f2059ce4a36a40a7a60b07c7e00";
        paperScreenShotService.startPaperScreenShotTask(projectId);
    }
}