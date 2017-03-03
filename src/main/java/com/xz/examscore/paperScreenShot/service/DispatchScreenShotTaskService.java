package com.xz.examscore.paperScreenShot.service;

import com.alibaba.fastjson.JSON;
import com.xz.ajiaedu.common.redis.Redis;
import com.xz.examscore.asynccomponents.QueueService;
import com.xz.examscore.paperScreenShot.bean.PaperScreenShotBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author by fengye on 2017/2/28.
 */
@Service
public class DispatchScreenShotTaskService {

    static final Logger LOG = LoggerFactory.getLogger(DispatchScreenShotTaskService.class);

    @Autowired
    Redis redis;

    @Autowired
    QueueService queueService;

    @Value("${paper.screenshot.task.list.key}")
    private String paperScreenShotTaskListKey;

    @Value("${paper.screenshot.task.counter.key}")
    private String paperScreenShotTaskCounterKey;

    @Value("${paper.screenshot.task.completed.counter.key}")
    private String PaperScreenShotTaskCompletedCounterKey;


    /**
     * 发布一个任务
     *
     * @param paperScreenShotBean
     */
    public void pushTask(PaperScreenShotBean paperScreenShotBean) {
        String key = "paperScreenShotTasks:c1";
        addToQueue(paperScreenShotBean, key);
        String taskKey = getTaskKey(paperScreenShotBean);
        redis.getHash(paperScreenShotTaskCounterKey + ":" + paperScreenShotBean.getTaskId()).incr(taskKey);
    }

    private String getTaskKey(PaperScreenShotBean paperScreenShotBean) {
        return paperScreenShotBean.getProjectId() + ":" + paperScreenShotBean.getSchoolId() + ":" + paperScreenShotBean.getClassId()
                + ":" + paperScreenShotBean.getSubjectId();
    }

    public void addToQueue(PaperScreenShotBean paperScreenShotBean, String key) {
        Redis.RedisQueue queue = redis.getQueue(key);
        queue.push(Redis.Direction.Left, JSON.toJSONString(paperScreenShotBean));
        LOG.debug("向队列 " + key + " 发送消息 " + paperScreenShotBean);
    }

}
