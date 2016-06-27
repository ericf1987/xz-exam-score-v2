package com.xz.services;

import com.alibaba.fastjson.JSON;
import com.xz.ajiaedu.common.redis.Redis;
import com.xz.mqreceivers.AggrTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    static final Logger LOG = LoggerFactory.getLogger(AggregationRoundService.class);

    @Autowired
    Redis redis;

    /**
     * 任务队列 key。任务队列是所有项目和所有统计共用的
     */
    @Value("${redis.task.list.key}")
    private String taskListKey;

    @Value("${redis.task.counter.key}")
    private String taskCounterKey;

    // 上次输出队列长度的时间
    private long lastQueueSizeLogTime = System.currentTimeMillis();

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
        redis.getHash(taskCounterKey + ":" + task.getAggregationId()).incr(task.getType());
    }

    /**
     * 当一个任务完成时的处理
     *
     * @param task 完成的任务
     */
    public void taskFinished(AggrTask task) {
        String aggregationId = task.getAggregationId();

        String taskType = task.getType();
        if (redis.getHash(taskCounterKey + ":" + aggregationId).incr(taskType, -1) == 0) {
            taskTypeFinished(aggregationId, taskType);
        }
    }

    public void taskTypeFinished(String aggregationId, String taskType) {
        redis.getList("completed_tasks:" + aggregationId).append(false, taskType);
    }

    /**
     * 从队列中取一个任务（阻塞）
     *
     * @return 取到的任务，null 表示没有任务
     */
    public String pickTask() {
        Redis.RedisQueue queue = redis.getQueue(taskListKey);
        String s = queue.popBlocking(Redis.Direction.Right, 3);
        if (s != null) {
            logTaskQueueSize(queue.size());
        }
        return s;
    }

    public void clearTask() {
        Redis.RedisQueue queue = redis.getQueue(taskListKey);
        queue.clear();
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

        logTaskQueueSize(queue.size());
        return aggrTask;
    }

    // 记录任务队列长度（每三秒钟记录一次）
    private void logTaskQueueSize(long size) {
        if (System.currentTimeMillis() - lastQueueSizeLogTime > 3000) {
            lastQueueSizeLogTime = System.currentTimeMillis();
            LOG.info("Redis 任务队列长度：" + size);
        }
    }

    // 等待本轮统计完成
    public void waitForRoundCompletion(String aggregationId) {
        Redis.RedisHash hash = redis.getHash(taskCounterKey + ":" + aggregationId);
        while (!allValuesAreZero(hash)) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                return;
            }
        }
        LOG.info("统计 {} 本轮完成。", aggregationId);
    }

    private boolean allValuesAreZero(Redis.RedisHash hash) {
        for (String key : hash.keys()) {
            if (!hash.get(key).equals("0")) {
                return false;
            }
        }
        return true;
    }
}
