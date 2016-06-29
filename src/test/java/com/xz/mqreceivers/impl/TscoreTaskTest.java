package com.xz.mqreceivers.impl;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.mqreceivers.AggrTask;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * (description)
 * created at 16/05/26
 *
 * @author yiding_he
 */
public class TscoreTaskTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    TscoreTask tscoreTask;

    @Test
    public void testRunTask() throws Exception {
        tscoreTask.runTask(new AggrTask(XT_PROJECT_ID, "111", "t_score")
                .setTarget(Target.subject("001")).setRange(Range.school("002e02d6-c036-4780-85d4-e54e3f1fbf9f")));
    }
}