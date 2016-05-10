package com.xz.controllers;

import com.xz.taskdispatchers.TaskDispatcher;
import com.xz.taskdispatchers.TaskDispatcherFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * (description)
 * created at 16/05/10
 *
 * @author yiding_he
 */
@Controller
@RequestMapping("/aggr")
public class AggregationTaskController {

    @Autowired
    TaskDispatcherFactory taskDispatcherFactory;

    /**
     * 开始统计任务
     *
     * @param projectId 项目ID
     * @param taskType  任务类型
     */
    @RequestMapping("/start")
    @ResponseBody
    public String startAggregationTask(
            @RequestParam("project") String projectId,
            @RequestParam("task") String taskType
    ) {
        TaskDispatcher taskDispatcher = taskDispatcherFactory.getTaskDispatcher(taskType);
        taskDispatcher.dispatch(projectId);

        return "项目 " + projectId + " 的任务 " + taskType + " 已经分发完毕。";
    }
}
