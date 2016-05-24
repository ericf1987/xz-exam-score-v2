package com.xz.mqreceivers.impl;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.bean.Range;
import com.xz.mqreceivers.AggrTask;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * (description)
 * created at 16/05/17
 *
 * @author yiding_he
 */
public class CombinedSubjectScoreTaskTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    CombinedSubjectScoreTask task;

    @Test
    public void testRunTask() throws Exception {
        String projectId = "430200-8a9be9fc2e1842a4b9b4894eee1f5f73";
        String type = "combined_total_score";

        task.runTask(new AggrTask(projectId, "aaa", type)
                .setRange(Range.student("a9e1be16-9fe9-4741-91ba-591587e15560")));
    }
}