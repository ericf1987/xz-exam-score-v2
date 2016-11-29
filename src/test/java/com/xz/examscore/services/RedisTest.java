package com.xz.examscore.services;

import com.xz.ajiaedu.common.redis.Redis;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.asynccomponents.QueueService;
import com.xz.examscore.asynccomponents.QueueType;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMessage;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2016/11/28.
 */
public class RedisTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    Redis redis;

    @Autowired
    QueueService queueService;

    @Test
    public void testHash() throws Exception {
        Redis.RedisHash redisHash = redis.getHash("aggregation_101");
        redisHash.put("total_score", String.valueOf(100));
        redisHash.incr("total_score", -1);
    }

    @Test
    public void testList() throws Exception {
        Redis.RedisQueue testQueue = redis.getQueue("testQueue");
        for (int i = 0; i <= 10; i++)
            testQueue.push(Redis.Direction.Left, "test" + i);
    }

    @Test
    public void testQueue() throws Exception {
        Range range = Range.clazz("131");
        Target target = Target.subject("001");
        AggrTaskMessage task = new AggrTaskMessage("100", "200", "total_score");
        task.setRange(range);
        task.setTarget(target);
        System.out.println(task.toString());
        queueService.addToQueue(QueueType.AggregationTaskList, task);
    }

    @Test
    public void testClearQueue() throws Exception {
        queueService.clearQueue(QueueType.AggregationTaskList);
    }
}
