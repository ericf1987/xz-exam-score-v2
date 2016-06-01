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
public class ScoreMapTaskTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    ScoreMapTask scoreMapTask;

    @Test
    public void testRunTask() throws Exception {
        String projectId = "430200-89c9dc7481cd47a69d85af3f0808e0c4";
        Range school = Range.school("7e34fa5e-9023-4ad4-b4fa-fe4e3d7d1b52");
        Target quest = Target.quest("573c49e62d560287556b8a76");

        scoreMapTask.runTask(new AggrTask(projectId, "aaaa", "score_map")
                .setRange(school)
                .setTarget(quest)
        );
    }
}