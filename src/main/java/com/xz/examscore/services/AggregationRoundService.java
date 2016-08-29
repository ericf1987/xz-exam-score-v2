package com.xz.examscore.services;

import com.xz.ajiaedu.common.redis.Redis;
import com.xz.examscore.asynccomponents.QueueService;
import com.xz.examscore.asynccomponents.QueueType;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 统计轮次管理
 *
 * @author yiding_he
 */
@Service
public class AggregationRoundService {

    static final Logger LOG = LoggerFactory.getLogger(AggregationRoundService.class);

    @Autowired
    Redis redis;

    @Autowired
    QueueService queueService;

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
    public void pushTask(AggrTaskMessage task) {
        queueService.addToQueue(QueueType.AggregationTaskList, task);
        redis.getHash(taskCounterKey + ":" + task.getAggregationId()).incr(task.getType());
    }

    /**
     * 当一个任务完成时的处理
     *
     * @param task 完成的任务
     */
    public void taskFinished(AggrTaskMessage task) {
        String aggregationId = task.getAggregationId();

        String taskType = task.getType();
        if (redis.getHash(taskCounterKey + ":" + aggregationId).incr(taskType, -1) == 0) {
            taskTypeFinished(aggregationId, taskType);
        }
    }

    public void taskTypeFinished(String aggregationId, String taskType) {
        redis.getList("completed_tasks:" + aggregationId).append(false, taskType);
    }

    public void clearTask() {
        queueService.clearQueue(QueueType.AggregationTaskList);
    }

    // 等待本轮统计完成
    void waitForRoundCompletion(String aggregationId) {
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
