package com.xz.examscore.asynccomponents.importproject;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.asynccomponents.QueueType;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * (description)
 * created at 16/08/25
 *
 * @author yiding_he
 */
public class ImportProjectMessageReceiverTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    ImportProjectMessageReceiver importProjectMessageReceiver;

    @Test
    public void testAutoReceive() throws Exception {
        importProjectMessageReceiver.queueService.addToQueue(
                QueueType.ImportTaskList, new ImportTaskMessage("TEST_123", true, true, false));

        Thread.sleep(60000);
    }

}