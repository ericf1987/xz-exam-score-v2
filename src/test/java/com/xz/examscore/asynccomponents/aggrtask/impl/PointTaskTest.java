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

    public static final String PROJECT = "433100-0372aa59ae4841618138c65e9ee18314";

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
                .setRange(Range.student("76b39443-8792-407e-bb2c-24c3aaa3eca0")));
    }
}