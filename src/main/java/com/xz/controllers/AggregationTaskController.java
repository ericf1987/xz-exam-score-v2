package com.xz.controllers;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.bean.ProjectStatus;
import com.xz.services.*;
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

    @Autowired
    PrepareDataService prepareDataService;

    @Autowired
    AggregationRoundService aggregationRoundService;

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

        // 照顾以前的 student_list
        if (taskType.equals("student_list")) {
            prepareDataService.prepare(projectId);
            return Result.success();
        }

        String aggregationId = UUID.randomUUID().toString();
        TaskDispatcher taskDispatcher = taskDispatcherFactory.getTaskDispatcher(taskType);

        if (taskDispatcher == null) {
            return Result.fail("找不到任务 " + taskType + " 的分发类");
        }

        aggregationService.runDispatchers(projectId, aggregationId, Collections.singletonList(taskDispatcher));
        return Result.success("项目 " + projectId + " 的任务 " + taskType + " 已经分发完毕。");
    }

    /**
     * 开始统计项目
     *
     * @param projectId      项目ID
     * @param recalculate    是否要重新计算成绩，如果为 true 则会重新导入题目信息；如果 dataReady 为 false 则忽略本参数
     * @param dataReady      数据是否已经存在，如果为 false 则会尝试重新导入整个项目信息
     * @param generateReport 是否要生成报表 Excel 文件
     */
    @RequestMapping(value = "/start/project", method = RequestMethod.POST)
    @ResponseBody
    public Result startAggregation(
            @RequestParam("project") String projectId,
            @RequestParam(value = "recalculate-score", required = false, defaultValue = "false") String recalculate,
            @RequestParam(value = "data-ready", required = false, defaultValue = "true") String dataReady,
            @RequestParam(value = "generate-report", required = false, defaultValue = "false") String generateReport
    ) {

        if (aggregationService.isAggregationRunning(projectId)) {
            return Result.fail("项目 " + projectId + " 正在统计当中");
        }

        Boolean isRecalculate = Boolean.valueOf(recalculate);
        Boolean isDataReady = Boolean.valueOf(dataReady);
        Boolean isGenerateReport = Boolean.valueOf(generateReport);

        aggregationService.startAggregation(projectId, true, isRecalculate, isDataReady, isGenerateReport);
        return Result.success("项目 " + projectId + " 已经开始统计。");
    }

    @RequestMapping("/project/status")
    @ResponseBody
    public Result getProjectStatus(@RequestParam("project") String projectId) {
        boolean running = aggregationService.isAggregationRunning(projectId);
        ProjectStatus projectStatus = projectStatusService.getProjectStatus(projectId);
        return Result.success().set("running", running).set("status", projectStatus.name());
    }

    @RequestMapping(value = "/clear/tasks", method = RequestMethod.POST)
    public Result clearRedisQueue() {
        aggregationRoundService.clearTask();
        return Result.success();
    }
}
