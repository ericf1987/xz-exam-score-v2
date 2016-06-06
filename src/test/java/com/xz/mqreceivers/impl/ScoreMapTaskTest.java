package com.xz.mqreceivers.impl;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.bean.Range;
import com.xz.mqreceivers.AggrTask;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * (description)
 * created at 16/05/13
 *
 * @author yiding_he
 */
public class ScoreMapTaskTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    ScoreMapTask scoreMapTask;

    @Test
    public void testRunTask() throws Exception {
        scoreMapTask.runTask(
                new AggrTask(PROJECT_ID, "aaaa", "score_map")
                        .setRange(Range.student("071158b5-493a-4f39-8fd9-a419a62b58d8")));
    }
}