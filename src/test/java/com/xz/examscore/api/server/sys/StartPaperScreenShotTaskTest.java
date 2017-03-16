package com.xz.examscore.api.server.sys;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.api.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2017/3/16.
 */
public class StartPaperScreenShotTaskTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    StartPaperScreenShotTask startPaperScreenShotTask;

    @Test
    public void testExecute() throws Exception {
        Param param = new Param().setParameter("projectId", "430900-8f11fe8dbac842a3805d45e05eb31095");
        Result result = startPaperScreenShotTask.execute(param);
        System.out.println(result.getData());
    }
}