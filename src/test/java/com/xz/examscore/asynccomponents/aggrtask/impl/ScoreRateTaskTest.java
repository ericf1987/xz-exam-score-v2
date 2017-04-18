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
        String projectId = "430100-354dce3ac8ef4800a1b57f81a10b8baa";
        String studentId = "4847b955-8c8e-4883-b9f6-2a4f42c44fe6";
        Range studentRange = Range.student(studentId);
        AggrTaskMessage atm = new AggrTaskMessage(projectId, "100", "score_rate");
        atm.setRange(studentRange);
        scoreRateTask.runTask(atm);
    }
}