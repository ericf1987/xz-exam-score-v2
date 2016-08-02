package com.xz.mqreceivers.impl;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.bean.Range;
import com.xz.mqreceivers.AggrTask;
import com.xz.services.PointService;
import com.xz.services.StudentService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/7/30.
 */
public class PointTaskTest extends XzExamScoreV2ApplicationTests{
    @Autowired
    StudentService studentService;

    @Autowired
    PointService pointService;

    @Autowired
    PointTask task;

    @Test
    public void testRunTask() throws Exception {
        task.runTask(
                new AggrTask("430100-e7bd093d92d844819c7eda8b641ab6ee", "1", "point")
                .setRange(Range.student("f6f290e7-3aaa-46dd-b169-c7d7edbf0516")));
    }
}