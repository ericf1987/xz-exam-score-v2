package com.xz.mqreceivers.impl;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.bean.Range;
import com.xz.mqreceivers.AggrTask;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * (description)
 * created at 16/05/17
 *
 * @author yiding_he
 */
public class CombinedSubjectScoreTaskTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    CombinedSubjectScoreTask task;

    @Test
    public void testRunTask() throws Exception {
        task.runTask(new AggrTask(PROJECT_ID, "aaa", "combined_total_score")
                .setRange(Range.student("SCHOOL_009_CLASS_03_02")));

    }
}