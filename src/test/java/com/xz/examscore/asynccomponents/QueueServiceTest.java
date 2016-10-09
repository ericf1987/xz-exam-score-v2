package com.xz.examscore.asynccomponents;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.services.QuestService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/10/9.
 */
public class QueueServiceTest extends XzExamScoreV2ApplicationTests {
    @Autowired
    QueueService queueService;

    @Test
    public void testGetQueueKey() throws Exception {
        queueService.clearQueue(QueueType.AggregationTaskList);
    }
}