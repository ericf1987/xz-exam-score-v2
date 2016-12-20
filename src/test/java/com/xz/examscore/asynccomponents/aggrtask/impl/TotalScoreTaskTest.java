package com.xz.examscore.asynccomponents.aggrtask.impl;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMessage;
import com.xz.examscore.bean.Target;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * (description)
 * created at 16/05/13
 *
 * @author yiding_he
 */
public class TotalScoreTaskTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    TotalScoreTask totalScoreTask;

    @Test
    public void testRunTask() throws Exception {
        AggrTaskMessage task = new AggrTaskMessage("430600-2404b0cc131c472dbbd13085385f5ee0", "aggr1", "total_score")
                .setTarget(Target.subjectCombination("004005006"));

        totalScoreTask.runTask(task);
    }
}