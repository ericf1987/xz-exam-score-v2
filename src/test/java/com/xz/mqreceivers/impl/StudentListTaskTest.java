package com.xz.mqreceivers.impl;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.mqreceivers.AggrTask;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * (description)
 * created at 16/05/20
 *
 * @author yiding_he
 */
public class StudentListTaskTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    StudentListTask studentListTask;

    @Test
    public void testRunTask() throws Exception {
        studentListTask.runTask(new AggrTask("430200-8a9be9fc2e1842a4b9b4894eee1f5f73", "aaa", "student_list"));
    }
}