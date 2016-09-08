package com.xz.examscore.asynccomponents.aggrtask.impl;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMessage;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.PointService;
import com.xz.examscore.services.StudentService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2016/7/30.
 */
public class PointTaskTest extends XzExamScoreV2ApplicationTests{

    public static final String PROJECT = "430100-a05db0d05ad14010a5c782cd31c0283f";

    @Autowired
    StudentService studentService;

    @Autowired
    PointService pointService;

    @Autowired
    PointTask task;

    @Test
    public void testRunTask() throws Exception {
        task.runTask(
                new AggrTaskMessage(PROJECT, "1", Target.SUBJECT_LEVEL)
                .setRange(Range.clazz("a1895cd9-d82c-4b12-a698-164fb5ceb1f3")));
    }
}