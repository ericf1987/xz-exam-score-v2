package com.xz.examscore.asynccomponents.aggrtask.impl;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMessage;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2017/5/9.
 */
public class ScoreSegmentTaskTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    ScoreSegmentTask scoreSegmentTask;

    public static final String PROJECT_ID = "431200-5c78e22cb1e64e4caa9583d35ad92658";

    public static final Range range = Range.province("430000");

    public static final Target target = Target.subject("009");

    @Test
    public void testRunTask() throws Exception {
        AggrTaskMessage atm = new AggrTaskMessage();
        atm.setProjectId(PROJECT_ID);
        atm.setRange(range);
        atm.setTarget(target);
        atm.setAggregationId("1111");
        atm.setType("score_segment");
        scoreSegmentTask.runTask(atm);
    }
}