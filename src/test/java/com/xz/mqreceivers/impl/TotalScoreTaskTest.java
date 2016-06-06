package com.xz.mqreceivers.impl;

import com.xz.XzExamScoreV2ApplicationTests;
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
public class TotalScoreTaskTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    TotalScoreTask totalScoreTask;

    @Test
    public void testRunTask() throws Exception {
        AggrTask task = new AggrTask(PROJECT_ID, "aggr1", "total_score")
                .setTarget(Target.subject("001"));

        totalScoreTask.runTask(task);
    }
}