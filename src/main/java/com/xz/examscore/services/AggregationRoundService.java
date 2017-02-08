package com.xz.examscore.services;

import com.hyd.appserver.utils.StringUtils;
import com.xz.ajiaedu.common.lang.StringUtil;
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

    @Value("${redis.task.list.completed.key}")
    private String completedTaskKey;

    @Value("${redis.aggregation.execution.start}")
    private String aggregationStartTime;

    @Value("${redis.task.execution.runtime}")
    private String taskRuntime;

    /**
     * 查询已经完成的任务类型
     *
     * @param aggregationId 本次统计ID
     * @return 已经完成的任务类型
     */
    public List<String> getCompletedTaskTypes(String aggregationId) {
        return redis.getList(completedTaskKey + ":" + aggregationId).all();
    }

    /**
     * 发布一个任务
     *
     * @param task 要发布的任务
     */
    public void pushTask(AggrTaskMessage task) {
        String taskType = task.getType();
        queueService.addToQueue(QueueType.AggregationTaskList, task);
        redis.getHash(taskCounterKey + ":" + task.getAggregationId()).incr(taskType);

        //如果该任务已经存在与taskRuntime中，增跳出任务
        if(StringUtils.isBlank(redis.getHash(taskRuntime + ":" + task.getAggregationId()).get(taskType))){
            queueService.recordAggrRunTime(taskType, task.getAggregationId());
        }
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
        redis.getList(completedTaskKey + ":" + aggregationId).append(false, taskType);
        //获取任务开始时间
        String beginTime = redis.getHash(taskRuntime + ":" + aggregationId).get(taskType);
        if (StringUtil.isBlank(beginTime)) {
            LOG.info("----无法查找任务的开始执行时间，统计任务不存在!{}", taskType);
            return;
        }
        //计算出执行时间
        long runTime = System.currentTimeMillis() - Long.valueOf(beginTime);
        redis.getHash(taskRuntime + ":" + aggregationId).put(taskType, String.valueOf(runTime));
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
