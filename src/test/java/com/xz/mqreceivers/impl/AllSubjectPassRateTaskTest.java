package com.xz.mqreceivers.impl;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.bean.Range;
import com.xz.mqreceivers.AggrTask;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * (description)
 * created at 16/05/23
 *
 * @author yiding_he
 */
public class AllSubjectPassRateTaskTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    AllSubjectPassRateTask allSubjectPassRateTask;

    @Test
    public void testRunTask() throws Exception {
        String projectId = "430200-8a9be9fc2e1842a4b9b4894eee1f5f73";
        String schoolId = "200f3928-a8bd-48c4-a2f4-322e9ffe3700";

        allSubjectPassRateTask.runTask(
                new AggrTask(projectId, "1", "all_subject_pass_rate").setRange(Range.school(schoolId)));
    }
}