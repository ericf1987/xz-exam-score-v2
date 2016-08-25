package com.xz.examscore.asynccomponents.aggrtask.impl;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskInfo;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * (description)
 * created at 16/06/06
 *
 * @author yiding_he
 */
public class RankPositionTaskTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    RankPositionTask rankPositionTask;

    @Test
    public void testRankPosition() throws Exception {
        rankPositionTask.runTask(new AggrTaskInfo("431100-903288f61a5547f1a08a7e20420c4e9e", "111", "rank_position")
                .setRange(Range.clazz("8918be00-4a5c-4f0d-bb3d-8ffa706a1891")).setTarget(Target.subject("001")));
    }
}