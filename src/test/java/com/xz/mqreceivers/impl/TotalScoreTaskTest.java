package com.xz.mqreceivers.impl;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.bean.Range;
import com.xz.bean.SubjectObjective;
import com.xz.bean.Target;
import com.xz.mqreceivers.AggrTask;
import com.xz.mqreceivers.ReceiverManager;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * (description)
 * created at 16/05/11
 *
 * @author yiding_he
 */
public class TotalScoreTaskTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    TotalScoreTask totalScoreTask;

    @Autowired
    ReceiverManager receiverManager;

    @Test
    public void testTaskReceived() throws Exception {
        totalScoreTask.taskReceived(
                new AggrTask(PROJECT_ID, "total_score")
                        .setRange(Range.CLASS, "SCHOOL_001_CLASS_01")
                        .setTarget(Target.SUBJECT_OBJECTIVE, new SubjectObjective("001", true))
        );
    }

    @Test
    public void testPickAndRunTask() throws Exception {
        receiverManager.pickOneTask("total_score");
    }
}