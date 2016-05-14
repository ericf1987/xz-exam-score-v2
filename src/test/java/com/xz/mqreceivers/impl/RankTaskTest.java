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
                .setRange(Range.clazz("SCHOOL_004_CLASS_04"))
                .setTarget(Target.quest("5732e1f6c5a637047a2f43f8"))
        );
    }
}