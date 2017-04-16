package com.xz.examscore.asynccomponents.aggrtask.impl;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMessage;
import com.xz.examscore.bean.Range;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2017/4/16.
 */
public class ScoreRateTaskTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    ScoreRateTask scoreRateTask;

    @Test
    public void testRunTask() throws Exception {
        String projectId = "430600-d248e561aefc425b9971f2a26d267478";
        String studentId = "9edff92f-eb7e-4b56-9f3c-62cffb3fa5d9";
        Range studentRange = Range.student(studentId);
        AggrTaskMessage atm = new AggrTaskMessage(projectId, "100", "score_rate");
        atm.setRange(studentRange);
        scoreRateTask.runTask(atm);
    }
}