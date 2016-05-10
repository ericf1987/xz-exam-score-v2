package com.xz.mqreceivers;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.bean.Range;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * (description)
 * created at 16/05/10
 *
 * @author yiding_he
 */
public class AverageTaskReceiverTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    AverageTaskReceiver averageTaskReceiver;

    @Test
    public void testTaskReceived() throws Exception {
        averageTaskReceiver.taskReceived(new AggrTask("FAKE_PROJECT_1", "average", new Range("area", "430101")));
    }
}