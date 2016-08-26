package com.xz.examscore.asynccomponents;

import com.alibaba.fastjson.JSON;
import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.ajiaedu.common.redis.Redis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 消息队列服务。本应用的所有消息都是拉取式的，也就是消息并不会明确的指定哪台服务器来处理。
 *
 * @author yiding_he
 */
@Component
public class QueueService {

    static final Logger LOG = LoggerFactory.getLogger(QueueService.class);

    @Autowired
    Redis redis;

    /**
     * 从队列中读取一条记录并转化为指定的 bean
     *
     * @param queueType      队列类型
     * @param timeoutSeconds 读取超时时间（秒）
     *
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
}
