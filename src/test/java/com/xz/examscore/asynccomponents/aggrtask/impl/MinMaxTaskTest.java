package com.xz.examscore.asynccomponents.aggrtask.impl;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskInfo;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * (description)
 * created at 16/05/13
 *
 * @author yiding_he
 */
public class MinMaxTaskTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    MinMaxTask minMaxTask;

    @Test
    public void testRunTask() throws Exception {
        minMaxTask.runTask(
                new AggrTaskInfo(XT_PROJECT_ID, "aggr1", "minmax")
                        .setRange(Range.SCHOOL, "SCHOOL_005")
                        .setTarget(Target.SUBJECT, "001"));
    }
}