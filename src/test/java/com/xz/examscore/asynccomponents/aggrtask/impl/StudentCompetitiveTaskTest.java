package com.xz.examscore.asynccomponents.aggrtask.impl;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMessage;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/12/29.
 */
public class StudentCompetitiveTaskTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    StudentCompetitiveTask studentCompetitiveTask;

    @Test
    public void testRunTask() throws Exception {
        AggrTaskMessage atm = new AggrTaskMessage();
        atm.setProjectId("430200-3e67c524f149491597279ef6ae31baef");
        atm.setRange(Range.clazz("a3fec3c6-0e46-40c3-8632-69bdf78d8484"));
        atm.setTarget(Target.subject("001"));
        atm.setType("student_competitive");
        atm.setAggregationId("888999");
        studentCompetitiveTask.runTask(atm);
    }
}