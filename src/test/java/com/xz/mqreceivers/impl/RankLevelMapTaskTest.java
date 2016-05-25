package com.xz.mqreceivers.impl;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.mqreceivers.AggrTask;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * (description)
 * created at 16/05/25
 *
 * @author yiding_he
 */
public class RankLevelMapTaskTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    RankLevelMapTask rankLevelMapTask;

    @Test
    public void testRunTask() throws Exception {
        String projectId = "430200-89c9dc7481cd47a69d85af3f0808e0c4";
        rankLevelMapTask.runTask(new AggrTask(projectId, "111", "rank_level_map")
                .setRange(Range.clazz("825d4b39-9934-4af2-9799-02536e8507aa"))
                .setTarget(Target.subject("001")));
    }
}