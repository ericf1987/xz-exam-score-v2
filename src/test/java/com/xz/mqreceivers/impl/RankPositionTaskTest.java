package com.xz.mqreceivers.impl;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.mqreceivers.AggrTask;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * (description)
 * created at 16/06/06
 *
 * @author yiding_he
 */
public class RankPositionTaskTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    RankPositionTask rankPositionTask;

    @Test
    public void testRankPosition() throws Exception {
        rankPositionTask.runTask(new AggrTask(PROJECT_ID, "111", "rank_position")
                .setRange(Range.province("430000")).setTarget(Target.subject("001")));
    }
}