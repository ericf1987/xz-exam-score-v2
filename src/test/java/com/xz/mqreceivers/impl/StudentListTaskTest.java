package com.xz.mqreceivers.impl;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.mqreceivers.AggrTask;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * (description)
 * created at 16/05/11
 *
 * @author yiding_he
 */
public class StudentListTaskTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    StudentListTask studentListTask;

    @Test
    public void testTaskReceived() throws Exception {
        studentListTask.taskReceived(new AggrTask(PROJECT_ID, "student_list"));
    }
}