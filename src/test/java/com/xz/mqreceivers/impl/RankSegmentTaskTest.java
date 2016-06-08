package com.xz.mqreceivers.impl;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.mqreceivers.AggrTask;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * (description)
 * created at 16/06/08
 *
 * @author yiding_he
 */
public class RankSegmentTaskTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    RankSegmentTask rankSegmentTask;

    @Test
    public void testRunTask() throws Exception {
        rankSegmentTask.runTask(new AggrTask(PROJECT_ID, "11", "rank_segment")
                .setRange(Range.school("11b66fc2-8a76-41c2-a1b3-5011523c7e47")).setTarget(Target.subject("001")));
    }
}