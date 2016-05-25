package com.xz.mqreceivers.impl;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.mqreceivers.AggrTask;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * (description)
 * created at 16/05/23
 *
 * @author yiding_he
 */
public class OptionMapTaskTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    OptionMapTask optionMapTask;

    @Test
    public void testRunTask() throws Exception {
        String projectId = "430200-89c9dc7481cd47a69d85af3f0808e0c4";
        String schoolId = "7e34fa5e-9023-4ad4-b4fa-fe4e3d7d1b52";
        String questId = "57403a032d560287556b90ca";
        optionMapTask.runTask(new AggrTask(projectId, "11", "option_count")
                .setRange(Range.school(schoolId)).setTarget(Target.quest(questId)));
    }
}