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
public class RankTaskTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    RankTask rankTask;

    @Test
    public void testRunTask() throws Exception {
        rankTask.runTask(new AggrTask(PROJECT_ID, "aaaa", "rank")
                .setRange(Range.school("SCHOOL_001"))
                .setTarget(Target.subject("001"))
        );
    }
}