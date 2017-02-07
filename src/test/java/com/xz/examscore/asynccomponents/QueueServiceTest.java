package com.xz.examscore.asynccomponents;

import com.xz.ajiaedu.common.redis.Redis;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.services.QuestService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/10/9.
 */
public class QueueServiceTest extends XzExamScoreV2ApplicationTests {
    @Autowired
    QueueService queueService;

    @Autowired
    Redis redis;

    @Test
    public void testGetQueueKey() throws Exception {
        queueService.clearQueue(QueueType.AggregationTaskList);
    }

    @Test
    public void test(){
/*        Set<String> keys = redis.keys("aggregation_task_count_*");
        for(String key : keys){
            redis.delete(key);
        }*/
        String key = "aggregation_task_count_tttttest:80934c78-7224-4a28-b817-5787325c1de0";
        redis.delete(key);
    }
}