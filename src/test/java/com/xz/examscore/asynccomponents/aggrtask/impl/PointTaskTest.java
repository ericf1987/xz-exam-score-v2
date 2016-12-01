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

    public static final String PROJECT = "430300-672a0ed23d9148e5a2a31c8bf1e08e62";

    @Autowired
    StudentService studentService;

    @Autowired
    PointService pointService;

    @Autowired
    PointTask task;

    @Test
    public void testRunTask() throws Exception {
        task.runTask(
                new AggrTaskMessage(PROJECT, "1", Target.POINT)
                .setRange(Range.student("09db6684-ba7c-435d-a430-70992c40fd0d")));
    }
}