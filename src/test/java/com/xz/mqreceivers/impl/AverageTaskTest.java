package com.xz.mqreceivers.impl;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.mqreceivers.AggrTask;
import com.xz.services.AverageService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * (description)
 * created at 16/05/16
 *
 * @author yiding_he
 */
public class AverageTaskTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    AverageTask averageTask;

    @Test
    public void testRunTask() throws Exception {
        averageTask.runTask(new AggrTask(PROJECT_ID, "1111", "average")
                .setTarget(Target.subject("001")).setRange(Range.school("SCHOOL_005")));
    }
}