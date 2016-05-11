package com.xz.mqreceivers.impl;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.bean.Target;
import com.xz.mqreceivers.AggrTask;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * (description)
 * created at 16/05/11
 *
 * @author yiding_he
 */
public class MinMaxTaskTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    MinMaxTask minMaxTask;

    @Test
    public void testTaskReceived() throws Exception {
        AggrTask aggrTask = new AggrTask(PROJECT_ID, "minmax")
                .setRange("area", "430101")
                .setTarget(new Target("project", PROJECT_ID));

        minMaxTask.taskReceived(aggrTask);
    }
}