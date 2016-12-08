package com.xz.examscore.asynccomponents.aggrtaskdispatcher;

import com.mongodb.client.FindIterable;
import com.xz.ajiaedu.common.lang.Context;
import com.xz.ajiaedu.common.lang.Value;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMessage;
import com.xz.examscore.bean.ProjectConfig;
import com.xz.examscore.bean.Range;
import com.xz.examscore.services.AggregationRoundService;
import com.xz.examscore.services.StudentService;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;

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

    @Autowired
    StudentService studentService;

    public void dispatch(Context context) {
        String projectId = context.get("projectId");
        String aggregationId = context.get("aggregationId");
        String taskType = getTaskType();

        taskCounter.set(Value.of(0));
        LOG.info("开始分发项目{}的{}统计任务,id={}", projectId, taskType, aggregationId);

        dispatch(
                projectId,
                aggregationId,
                context.get("projectConfig"),
                context.get("rangesMap")
        );

        Integer taskCount = taskCounter.get().get();
        LOG.info("项目{}的{}统计任务分发完毕，共分发{}条任务", projectId, taskType, taskCount);

        if (taskCount == 0) {
            aggregationRoundService.taskTypeFinished(aggregationId, taskType);
        }
    }

    public abstract void dispatch(String projectId, String aggregationId, ProjectConfig projectConfig, Map<String, List<Range>> rangesMap);

//    public abstract void dispatch(String projectId, String aggregationId, ProjectConfig projectConfig);

    public TaskDispatcherInfo getInfo() {
        if (!this.getClass().isAnnotationPresent(TaskDispatcherInfo.class)) {
            return null;
        } else {
            return this.getClass().getAnnotation(TaskDispatcherInfo.class);
        }
    }

    @PostConstruct
    private void init() {
        taskDispatcherFactory.registerTaskDispatcher(this);
    }

    protected AggrTaskMessage createTask(String projectId, String aggregationId) {
        return new AggrTaskMessage(projectId, aggregationId, this.getClass().getAnnotation(TaskDispatcherInfo.class).taskType());
    }

    protected void dispatchTask(AggrTaskMessage task) {
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

    //////////////////////////////////////////////////////////////

    /**
     * 每个考生发布一个任务，这个任务只有 Range
     *
     * @param projectId      项目ID
     * @param aggregationId  统计ID
     * @param beforeDispatch 发布每个任务之前需要进行的处理
     */
    protected void dispatchTaskForEveryStudent(
            String projectId, String aggregationId, Consumer<String> beforeDispatch) {

        FindIterable<Document> list = this.studentService.getProjectStudentList(projectId, null, 0, 0, doc("student", 1));
        for (Document document : list) {
            String studentId = document.getString("student");
            if (beforeDispatch != null) {
                beforeDispatch.accept(studentId);
            }
            dispatchTask(createTask(projectId, aggregationId).setRange(Range.student(studentId)));
        }
    }

    // 每个考生发布一个任务，这个任务只有 Range
    protected void dispatchTaskForEveryStudent(String projectId, String aggregationId) {
        dispatchTaskForEveryStudent(projectId, aggregationId, null);
    }

    public List<Range> fetchRanges(String[] rangeKeys, Map<String, List<Range>> rangesMap) {
        List<Range> ranges = new ArrayList<>();
        for (String rangeKey : rangeKeys) {
            ranges.addAll(rangesMap.get(rangeKey));
        }
        return ranges;
    }
}
