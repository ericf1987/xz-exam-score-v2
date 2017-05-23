package com.xz.examscore.asynccomponents.aggrtask.impl;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMessage;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
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
        String projectId = "430100-354dce3ac8ef4800a1b57f81a10b8baa";
        String student = "06b6737f-303f-4c9b-8d86-6c5c3e21b1d2";
        AggrTaskMessage taskInfo = new AggrTaskMessage(projectId, "aaa", "rank_level").setRange(Range.student(student));

        rankLevelTask.runTask(taskInfo);
    }

    @Test
    public void testsaveRankLevel() throws Exception {
        String projectId = "430200-83943be3c36f43a8aab0b545e66dbe3d";
        String student = "c6864656-f372-4ffa-91ba-50bcb56dad1a";
        Range schoolRange = Range.school("7e34fa5e-9023-4ad4-b4fa-fe4e3d7d1b52");
        Range classRange = Range.clazz("fbc457d7-865c-4599-86e3-baf857f7a75d");
        Target target = Target.subject("002");

        rankLevelTask.saveRankLevel(projectId, student, classRange, target);
    }
}