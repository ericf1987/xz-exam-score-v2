package com.xz.mqreceivers.impl;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.mqreceivers.AggrTask;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * (description)
 * created at 16/05/16
 *
 * @author yiding_he
 */
public class RankingLevelTaskTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    RankingLevelTask rankingLevelTask;

    @Test
    public void testRunTask() throws Exception {
        rankingLevelTask.runTask(new AggrTask(PROJECT_ID, "aaa", "ranking_level")
                .setRange(Range.clazz("SCHOOL_009_CLASS_03")).setTarget(Target.subject("004005006")));
    }
}