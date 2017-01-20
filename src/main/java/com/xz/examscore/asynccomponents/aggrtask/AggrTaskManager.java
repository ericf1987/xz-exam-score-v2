package com.xz.examscore.asynccomponents.aggrtask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 本类功能：
 * 1、注册 AggrTask 实例；
 * 2、查询并执行 AggrTask 实例
 *
 * @author yiding_he
 */
@Component
public class AggrTaskManager {

    static final Logger LOG = LoggerFactory.getLogger(AggrTaskManager.class);

    private Map<String, AggrTask> taskInstanceMap = new HashMap<>();

    void handleMessage(AggrTaskMessage message) {
        String commandType = message.getType();
        AggrTask aggrTask = taskInstanceMap.get(commandType);

        if (aggrTask != null) {
            aggrTask.taskReceived(message);
        } else {
            LOG.error("无法执行消息(没有匹配的 AggrTask 对象) " + message);
        }
    }

    void register(AggrTask aggrTask) {
        AggrTaskMeta info = aggrTask.getClass().getAnnotation(AggrTaskMeta.class);

        if (info == null) {
            throw new IllegalArgumentException("Task " + aggrTask.getClass().getName() + " not annotated.");
        }

        taskInstanceMap.put(info.taskType(), aggrTask);
    }

}
