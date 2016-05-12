package com.xz.services;

import com.alibaba.fastjson.JSON;
import com.xz.ajiaedu.common.redis.Redis;
import com.xz.mqreceivers.AggrTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * (description)
 * created at 16/05/12
 *
 * @author yiding_he
 */
@Service
public class AggregationRoundService {

    @Autowired
    Redis redis;

    /**
     * 任务队列 key。任务队列是所有项目和所有统计共用的
     */
    @Value("${redis.task.list.key}")
    private String taskListKey;

    @Value("${redis.task.counter.key}")
    private String taskCounterKey;

    /**
     * 查询已经完成的任务类型
     *
     * @param aggregationId 本次统计ID
     *
     * @return 已经完成的任务类型
     */
    public List<String> getCompletedTaskTypes(String aggregationId) {
        return redis.getList("completed_tasks:" + aggregationId).all();
    }

    /**
     * 发布一个任务
     *
     * @param task 要发布的任务
     */
    public void pushTask(AggrTask task) {
        redis.getQueue(taskListKey).push(Redis.Direction.Left, JSON.toJSONString(task));
        redis.getCounter(taskCounterKey + ":" + task.getAggregationId(), 0).incr();
    }

    /**
     * 当一个任务完成时的处理
     *
     * @param task 完成的任务
     */
    public void taskFinished(AggrTask task) {
        redis.getHash(taskCounterKey + ":" + task.getAggregationId()).incr(task.getType(), -1);
        redis.getCounter(taskCounterKey + ":" + task.getAggregationId(), 0).add(-1);
    }

    /**
     * 从队列中取一个任务（阻塞）
     *
     * @return 取到的任务，null 表示没有任务
     */
    public String pickTask() {
        return redis.getQueue(taskListKey).popBlocking(Redis.Direction.Right, 3);
    }

    /**
     * 从队列中取指定类型的任务，会一直阻塞直到取到任务为止。用于单元测试
     *
     * @param taskType 指定类型
     *
     * @return 指定类型的任务
     */
    public AggrTask pickOneTask(String taskType) {
        Redis.RedisQueue queue = redis.getQueue(taskListKey);
        AggrTask aggrTask = null;

        while (aggrTask == null) {
            String taskJson = queue.popBlocking(Redis.Direction.Right, 3);
            while (taskJson == null) {
                taskJson = queue.popBlocking(Redis.Direction.Right, 3);
            }

            aggrTask = JSON.parseObject(taskJson, AggrTask.class);
            if (!aggrTask.getType().equals(taskType)) {
                aggrTask = null;
            }
        }

        return aggrTask;
    }

    public void waitForRoundCompletion(String aggregationId) {
        Redis.RedisCounter counter = redis.getCounter(taskCounterKey + ":" + aggregationId);
        while (counter.get() > 0) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}
