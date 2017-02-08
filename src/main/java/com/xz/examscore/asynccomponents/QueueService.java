package com.xz.examscore.asynccomponents;

import com.alibaba.fastjson.JSON;
import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.ajiaedu.common.redis.Redis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 消息队列服务。本应用的所有消息都是拉取式的，也就是消息并不会明确的指定哪台服务器来处理。
 *
 * @author yiding_he
 */
@Component
public class QueueService {

    static final Logger LOG = LoggerFactory.getLogger(QueueService.class);

    private final static int REDIS_KEY_LIVE_CYCLE = 1000 * 60 * 60 * 24;

    @Autowired
    Redis redis;

    @Value("${redis.task.counter.key}")
    private String taskCounterKey;

    @Value("${redis.task.list.completed.key}")
    private String completedTaskKey;

    @Value("${redis.aggregation.execution.start}")
    private String aggregationStartTime;

    @Value("${redis.task.execution.runtime}")
    private String taskRuntime;

    /**
     * 从队列中读取一条记录并转化为指定的 bean
     *
     * @param queueType      队列类型
     * @param timeoutSeconds 读取超时时间（秒）
     * @return 读取结果，如果没有则返回 null
     */
    public <T extends QueueMessage> T readFromQueue(QueueType queueType, int timeoutSeconds) {
        String queueKey = getQueueKey(queueType);
        Class<T> beanType = (Class<T>) queueType.getMessageObjectType();
        Redis.RedisQueue queue = redis.getQueue(queueKey);
        String json = queue.popBlocking(Redis.Direction.Right, timeoutSeconds);
        return json == null ? null : JSON.parseObject(json, beanType);
    }

    /**
     * 将消息对象放入队列
     *
     * @param queueType 队列类型
     * @param value     消息对象
     */
    public void addToQueue(QueueType queueType, QueueMessage value) {
        String queueKey = getQueueKey(queueType);
        Redis.RedisQueue queue = redis.getQueue(queueKey);
        queue.push(Redis.Direction.Left, JSON.toJSONString(value));

        LOG.debug("向队列 " + queueKey + " 发送消息 " + value);
    }

    private String getQueueKey(QueueType queueType) {
        String suffix = StringUtil.or(System.getProperty("cluster"), "");
        return queueType.name() + ":" + suffix;
    }

    /**
     * 清空指定队列
     *
     * @param queueType 队列类型
     */
    public void clearQueue(QueueType queueType) {
        String queueKey = getQueueKey(queueType);
        Redis.RedisQueue queue = redis.getQueue(queueKey);
        queue.clear();
    }

    /**
     * 清空统计队列keys
     */
    public void deleteByKey(String aggregationKey) {
        Set<String> keys = redis.keys(aggregationKey);
        if (keys.isEmpty()) {
            LOG.info("未找到数据,{}", aggregationKey);
            return;
        }
        for (String key : keys) {
            LOG.info("当前key-->{}", key);
            redis.delete(key);
        }
        LOG.info("成功清理{}条数据", keys.size());
    }

    /**
     * 记录统计时间
     */
    public void recordAggrTime(String uuid) {
        long currentTimeMillis = System.currentTimeMillis();
        redis.set(aggregationStartTime + ":" + uuid, String.valueOf(currentTimeMillis));
    }

    /**
     * 判断统计是否逾期
     */
    public boolean isAggrOverdue(String key) {
        long currentTimeMillis = System.currentTimeMillis();
        long startTime = Long.valueOf(redis.get(key));
        //时间暂定24小时
        if (currentTimeMillis - startTime >= REDIS_KEY_LIVE_CYCLE) {
            return true;
        }
        return false;
    }

    public void clearOverdueAggr() {
        redis.scan("aggr_start_time:*", (r, key) -> {
            if (isAggrOverdue(key)) {
                LOG.info("该统计任务{}已逾期，执行清理...", key);
                String[] redisKeys = getRedisKeys(key.split(":")[1]);
                redis.delete(redisKeys);
            }
        });
    }

    public String[] getRedisKeys(String uuid) {
        String[] keys = new String[]{
                taskCounterKey + ":" + uuid,
                completedTaskKey + ":" + uuid,
                aggregationStartTime + ":" + uuid,
                taskRuntime + ":" + uuid
        };
        return keys;
    }

    /**
     * 记录统计执行时间
     * @param commandType 统计任务类型
     * @param aggregationId 轮次ID
     */
    public void recordAggrRunTime(String commandType, String aggregationId) {
        redis.getHash(taskRuntime + ":" + aggregationId).put(commandType, String.valueOf(System.currentTimeMillis()));
    }
}
