package com.xz.taskdispatchers;

import com.xz.ajiaedu.common.lang.Context;
import com.xz.ajiaedu.common.lang.Value;
import com.xz.bean.ProjectConfig;
import com.xz.mqreceivers.AggrTask;
import com.xz.services.AggregationRoundService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

/**
 * (description)
 * created at 16/05/10
 *
 * @author yiding_he
 */
public abstract class TaskDispatcher {

    static final Logger LOG = LoggerFactory.getLogger(TaskDispatcher.class);

    private static final ThreadLocal<Value<Integer>> taskCounter = new ThreadLocal<>();

    @Autowired
    TaskDispatcherFactory taskDispatcherFactory;

    @Autowired
    AggregationRoundService aggregationRoundService;

    public void dispatch(Context context) {
        String projectId = context.get("projectId");
        String aggregationId = context.get("aggregationId");
        String taskType = getTaskType();

        taskCounter.set(Value.of(0));
        LOG.info("开始分发项目{}的{}统计任务,id={}", projectId, taskType, aggregationId);

        dispatch(
                projectId,
                aggregationId,
                context.get("projectConfig")
        );

        LOG.info("项目{}的{}统计任务分发完毕，共分发{}条任务", projectId, taskType, taskCounter.get().get());
    }

    public abstract void dispatch(String projectId, String aggregationId, ProjectConfig projectConfig);

    @PostConstruct
    private void init() {
        taskDispatcherFactory.registerTaskDispatcher(this);
    }

    protected AggrTask createTask(String projectId, String aggregationId) {
        return new AggrTask(projectId, aggregationId, this.getClass().getAnnotation(TaskDispatcherInfo.class).taskType());
    }

    protected void dispatchTask(AggrTask task) {
        Value<Integer> counterValue = taskCounter.get();
        counterValue.set(counterValue.get() + 1);

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
