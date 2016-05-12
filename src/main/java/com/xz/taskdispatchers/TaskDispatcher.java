package com.xz.taskdispatchers;

import com.xz.mqreceivers.AggrTask;
import com.xz.services.AggregationRoundService;
import org.springframework.beans.factory.annotation.Autowired;

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
    AggregationRoundService aggregationRoundService;

    public abstract void dispatch(String projectId, String aggregationId);

    @PostConstruct
    private void init() {
        taskDispatcherFactory.registerTaskDispatcher(this);
    }

    protected AggrTask createTask(String projectId, String aggregationId) {
        return new AggrTask(projectId, aggregationId, this.getClass().getAnnotation(TaskDispatcherInfo.class).taskType());
    }

    protected void dispatchTask(AggrTask task) {
        aggregationRoundService.pushTask(task);
    }

    public String getTaskType() {
        TaskDispatcherInfo info = this.getClass().getAnnotation(TaskDispatcherInfo.class);
        return info == null ? null : info.taskType();
    }

    /**
     * 获取依赖任务类型
     *
     * @return 如果没有依赖任务类型则返回 null
     */
    public String getDependentTaskType() {
        TaskDispatcherInfo info = this.getClass().getAnnotation(TaskDispatcherInfo.class);
        return info == null ? null : (info.dependentTaskType().equals("") ? null : info.dependentTaskType());
    }
}
