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
public class TotalScoreTaskTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    TotalScoreTask totalScoreTask;

    @Test
    public void testRunTask() throws Exception {
        AggrTask task = new AggrTask("430200-89c9dc7481cd47a69d85af3f0808e0c4", "aggr1", "total_score")
                .setRange(new Range("province", "430000"))
                .setTarget(new Target("quest", "5732e1f6c5a637047a2f4406"));

        totalScoreTask.runTask(task);
    }
}