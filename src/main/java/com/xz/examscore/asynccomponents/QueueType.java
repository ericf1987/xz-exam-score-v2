package com.xz.examscore.asynccomponents;

import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMessage;
import com.xz.examscore.asynccomponents.aggrtaskdispatcher.DispatchTaskMessage;
import com.xz.examscore.asynccomponents.importproject.ImportTaskMessage;
import com.xz.examscore.asynccomponents.report.ReportTaskMessage;

/**
 * 消息队列的类型
 *
 * @author yiding_he
 */
public enum QueueType {

    /**
     * 导入项目命令
     */
    ImportTaskList(ImportTaskMessage.class),

    /**
     * 开始执行统计命令（生成并分发计算统计指标命令）
     */
    DispatchTaskList(DispatchTaskMessage.class),

    /**
     * 计算统计指标命令
     */
    AggregationTaskList(AggrTaskMessage.class),

    /**
     * 生成报表命令
     */
    ReportTaskList(ReportTaskMessage.class);

    //////////////////////////////////////////////////////////////

    private Class<? extends QueueMessage> messageObjectType;

    QueueType(Class<? extends QueueMessage> messageObjectType) {
        this.messageObjectType = messageObjectType;
    }

    public Class<?> getMessageObjectType() {
        return messageObjectType;
    }

    public static QueueType valueOf(Class<? extends QueueMessage> messageType) {

        for (QueueType queueType : values()) {
            if (messageType == queueType.messageObjectType) {
                return queueType;
            }
        }

        return null;
    }
}
