package com.xz.examscore.asynccomponents.aggrtaskdispatcher.impl;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * (description)
 * created at 16/07/11
 *
 * @author yiding_he
 */
public class PointDispatcherTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    PointDispatcher pointDispatcher;

    @Test
    public void testDeleteOldData() throws Exception {
        pointDispatcher.deleteOldData("430200-b73f03af1d74484f84f1aa93f583caaa");
    }
}