package com.xz.examscore.asynccomponents.aggrtask;

import com.xz.examscore.asynccomponents.MessageReceiver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 获取数据统计项消息并执行
 *
 * @author yiding_he
 */
@Component
public class AggrMessageReceiver extends MessageReceiver<AggrTaskMessage> {

    @Autowired
    private AggrTaskManager aggrTaskManager;

    @Override
    protected void executeTask(AggrTaskMessage message) {
        aggrTaskManager.handleMessage(message);
    }
}
