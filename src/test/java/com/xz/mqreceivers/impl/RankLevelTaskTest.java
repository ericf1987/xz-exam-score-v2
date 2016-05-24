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
        String student = "bbe2e6dd-200d-480f-a93e-152c2209a8f9";
        String projectId = "430200-89c9dc7481cd47a69d85af3f0808e0c4";
        AggrTask aggrTask = new AggrTask(projectId, "aaa", "ranking_level").setRange(Range.student(student));

        rankLevelTask.runTask(aggrTask);
    }
}