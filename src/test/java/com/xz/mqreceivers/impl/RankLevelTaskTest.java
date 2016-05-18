package com.xz.mqreceivers.impl;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.bean.Range;
import com.xz.mqreceivers.AggrTask;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * (description)
 * created at 16/05/16
 *
 * @author yiding_he
 */
public class RankLevelTaskTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    RankLevelTask rankLevelTask;

    @Test
    public void testRunTask() throws Exception {
        String student = "SCHOOL_009_CLASS_03_02";
        rankLevelTask.runTask(new AggrTask(PROJECT_ID, "aaa", "ranking_level").setRange(Range.student(student)));
    }
}