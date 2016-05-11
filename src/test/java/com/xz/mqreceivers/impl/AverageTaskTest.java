package com.xz.mqreceivers.impl;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.bean.Range;
import com.xz.mqreceivers.AggrTask;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * (description)
 * created at 16/05/10
 *
 * @author yiding_he
 */
public class AverageTaskTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    AverageTask averageTask;

    @Test
    public void testTaskReceived() throws Exception {
        averageTask.taskReceived(new AggrTask("FAKE_PROJECT_1", "average", new Range("area", "430101")));
    }
}