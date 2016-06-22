package com.xz.controllers;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.bean.ProjectStatus;
import com.xz.services.AggregationService;
import com.xz.services.ProjectConfigService;
import com.xz.services.ProjectStatusService;
import com.xz.taskdispatchers.TaskDispatcher;
import com.xz.taskdispatchers.TaskDispatcherFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
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

    @Autowired
    ProjectStatusService projectStatusService;

    /**
     * 开始统计任务
     *
     * @param projectId 项目ID
     * @param taskType  任务类型
     */
    @RequestMapping(value = "/start/task", method = RequestMethod.POST)
    @ResponseBody
    public Result startAggregationTask(
            @RequestParam("project") String projectId,
            @RequestParam("task") String taskType
    ) {

        String aggregationId = UUID.randomUUID().toString();
        TaskDispatcher taskDispatcher = taskDispatcherFactory.getTaskDispatcher(taskType);
        aggregationService.runDispatchers(projectId, aggregationId, Collections.singletonList(taskDispatcher));
        return Result.success("项目 " + projectId + " 的任务 " + taskType + " 已经分发完毕。");
    }

    /**
     * 开始统计项目
     *
     * @param projectId 项目ID
     * @param dataReady 数据是否已经存在，如果为 false 则会尝试重新导入整个项目信息
     */
    @RequestMapping(value = "/start/project", method = RequestMethod.POST)
    @ResponseBody
    public Result startAggregation(
            @RequestParam("project") String projectId,
            @RequestParam(value = "data-ready", required = false, defaultValue = "true") String dataReady,
            @RequestParam(value = "generate-report", required = false, defaultValue = "false") String generateReport
    ) {

        if (aggregationService.isAggregationRunning(projectId)) {
            return Result.fail("项目 " + projectId + " 正在统计当中");
        }

        Boolean isDataReady = Boolean.valueOf(dataReady);
        Boolean isGenerateReport = Boolean.valueOf(generateReport);

        aggregationService.startAggregation(projectId, true, isDataReady, isGenerateReport);
        return Result.success("项目 " + projectId + " 已经开始统计。");
    }

    @RequestMapping("/project/status")
    @ResponseBody
    public Result getProjectStatus(@RequestParam("project") String projectId) {
        boolean running = aggregationService.isAggregationRunning(projectId);
        ProjectStatus projectStatus = projectStatusService.getProjectStatus(projectId);
        return Result.success().set("running", running).set("status", projectStatus.name());
    }
}
