package com.xz.controllers;

import com.xz.bean.ProjectConfig;
import com.xz.services.AggregationService;
import com.xz.services.ProjectConfigService;
import com.xz.taskdispatchers.TaskDispatcher;
import com.xz.taskdispatchers.TaskDispatcherFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.UUID;

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

    @Autowired
    AggregationService aggregationService;

    @Autowired
    ProjectConfigService projectConfigService;

    /**
     * 开始统计任务
     *
     * @param projectId 项目ID
     * @param taskType  任务类型
     */
    @RequestMapping("/start/task")
    @ResponseBody
    public String startAggregationTask(
            @RequestParam("project") String projectId,
            @RequestParam("task") String taskType
    ) {

        ProjectConfig projectConfig = projectConfigService.getProjectConfig(projectId);

        TaskDispatcher taskDispatcher = taskDispatcherFactory.getTaskDispatcher(taskType);
        taskDispatcher.dispatch(projectId, UUID.randomUUID().toString(), projectConfig);

        return "项目 " + projectId + " 的任务 " + taskType + " 已经分发完毕。";
    }

    @RequestMapping("/start/project")
    @ResponseBody
    public String startAggregation(@RequestParam("project") String projectId) {
        aggregationService.startAggregation(projectId, true);
        return "项目 " + projectId + " 已经开始统计。";
    }
}
