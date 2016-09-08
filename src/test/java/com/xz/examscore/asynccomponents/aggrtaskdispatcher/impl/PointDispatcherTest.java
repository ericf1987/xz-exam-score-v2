package com.xz.examscore.asynccomponents.aggrtaskdispatcher.impl;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.services.ProjectConfigService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * (description)
 * created at 16/07/11
 *
 * @author yiding_he
 */
public class PointDispatcherTest extends XzExamScoreV2ApplicationTests {

    public static final String PROJECT = "430100-a05db0d05ad14010a5c782cd31c0283f";

    @Autowired
    PointDispatcher pointDispatcher;

    @Autowired
    ProjectConfigService projectConfigService;

    @Test
    public void testDeleteOldData() throws Exception {
        pointDispatcher.deleteOldData("430200-b73f03af1d74484f84f1aa93f583caaa");
    }

    @Test
    public void testRunDispatch() throws Exception {
        pointDispatcher.dispatch(PROJECT, "1", projectConfigService.getProjectConfig(PROJECT));
    }
}