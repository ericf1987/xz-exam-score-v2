package com.xz.examscore.asynccomponents;

import com.xz.ajiaedu.common.redis.Redis;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.services.QuestService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

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

    @Value("${redis.task.list.key}")
    private String taskListKey;

    @Value("${redis.task.counter.key}")
    private String taskCounterKey;

    @Value("${redis.task.list.completed.key}")
    private String completedTaskKey;

    @Value("${redis.aggregation.execution.start}")
    private String aggregationStartTime;

    @Value("${redis.task.execution.runtime}")
    private String taskRuntime;

    @Test
    public void testGetQueueKey() throws Exception {
        queueService.clearQueue(QueueType.AggregationTaskList);
    }

    @Test
    public void test() {
/*        Set<String> keys = redis.keys("aggregation_task_count_*");
        for(String key : keys){
            redis.delete(key);
        }*/
        String key = "aggregation_task_count_tttttest:80934c78-7224-4a28-b817-5787325c1de0";
        redis.delete(key);
    }

    @Test
    public void test1() throws Exception {
        //记录统计时间
        queueService.recordAggrTime("3f4cfbff-9dcc-4470-bf2f-b8d7ee55974e");
        //判断是否逾期
        System.out.println(queueService.isAggrOverdue("aggr_start_time:10000"));
        //删除任务
        queueService.clearOverdueAggr();
    }

    @Test
    public void test2() throws Exception {
        String aggregationId = "8fe471cf-a067-49e5-9543-18e3c0f8122e";
        String beginTime = redis.getHash(taskRuntime + ":" + aggregationId).get("point");
        System.out.println(beginTime);
        long runTime = System.currentTimeMillis() - Long.valueOf(beginTime);
        System.out.println(runTime);
        redis.getHash(taskRuntime + ":" + aggregationId).put("point", String.valueOf(runTime));
    }
}