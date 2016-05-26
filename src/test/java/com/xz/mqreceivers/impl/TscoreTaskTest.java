package com.xz.mqreceivers.impl;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.mqreceivers.AggrTask;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * (description)
 * created at 16/05/26
 *
 * @author yiding_he
 */
public class TscoreTaskTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    TscoreTask tscoreTask;

    @Test
    public void testRunTask() throws Exception {
        tscoreTask.runTask(new AggrTask("430200-89c9dc7481cd47a69d85af3f0808e0c4", "111", "t_score")
                .setTarget(Target.subject("001")).setRange(Range.school("7e34fa5e-9023-4ad4-b4fa-fe4e3d7d1b52")));
    }
}