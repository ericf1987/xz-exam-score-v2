package com.xz.mqreceivers.impl;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.bean.Range;
import com.xz.mqreceivers.AggrTask;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * (description)
 * created at 16/06/11
 *
 * @author yiding_he
 */
public class QuestTypeScoreTaskTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    QuestTypeScoreTask task;

    @Test
    public void testRunTask() throws Exception {
        task.runTask(new AggrTask(PROJECT_ID, "1", "quest_type_score")
                .setRange(Range.student("000517ac-9277-4795-b0e7-0e9236b0e0b0")));
    }
}