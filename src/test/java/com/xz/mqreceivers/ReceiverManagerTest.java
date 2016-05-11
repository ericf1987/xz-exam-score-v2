package com.xz.mqreceivers;

import com.xz.XzExamScoreV2ApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * (description)
 * created at 16/05/11
 *
 * @author yiding_he
 */
public class ReceiverManagerTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    ReceiverManager receiverManager;

    @Test
    public void testListTasks() throws Exception {
        receiverManager.listTasks();
    }
}