package com.xz.taskdispatchers;

import com.alibaba.fastjson.JSON;
import com.xz.ajiaedu.common.redis.Redis;
import com.xz.ajiaedu.common.redis.Redis.Direction;
import com.xz.mqreceivers.AggrTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;

/**
 * (description)
 * created at 16/05/10
 *
 * @author yiding_he
 */
public abstract class TaskDispatcher {

    @Autowired
    TaskDispatcherFactory taskDispatcherFactory;

    @Autowired
    Redis redis;

    @Value("${redis.task.list.key}")
    private String listKey;

    public abstract void dispatch(String projectId);

    @PostConstruct
    private void init() {
        taskDispatcherFactory.registerTaskDispatcher(this);
    }

    protected AggrTask createTask(String projectId) {
        return new AggrTask(projectId, this.getClass().getAnnotation(TaskDispatcherInfo.class).taskType());
    }

    protected void dispatchTask(AggrTask task) {
        redis.getQueue(listKey).push(Direction.Left, JSON.toJSONString(task));
    }

}
