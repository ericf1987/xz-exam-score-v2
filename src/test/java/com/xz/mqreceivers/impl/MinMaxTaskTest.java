package com.xz.mqreceivers.impl;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.mqreceivers.AggrTask;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * (description)
 * created at 16/05/13
 *
 * @author yiding_he
 */
public class MinMaxTaskTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    MinMaxTask minMaxTask;

    @Test
    public void testRunTask() throws Exception {
        minMaxTask.runTask(
                new AggrTask(PROJECT_ID, "aggr1", "minmax")
                        .setRange(Range.SCHOOL, "SCHOOL_005")
                        .setTarget(Target.SUBJECT, "001"));
    }
}