package com.xz.examscore.controllers;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.asynccomponents.aggrtaskdispatcher.TaskDispatcher;
import com.xz.examscore.asynccomponents.aggrtaskdispatcher.TaskDispatcherFactory;
import com.xz.examscore.bean.AggregationConfig;
import com.xz.examscore.bean.AggregationStatus;
import com.xz.examscore.bean.AggregationType;
import com.xz.examscore.bean.ProjectStatus;
import com.xz.examscore.services.*;
import org.bson.Document;
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
    ProjectService projectService;

    @Autowired
    PrepareDataService prepareDataService;

    @Autowired
    AggregationRoundService aggregationRoundService;

    @Autowired
    CleanProjectService cleanProjectService;

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
     * @param projectId       项目ID
     * @param type            统计类型（参考 {@link AggregationType}）:basic
     * @param reimportProject 是否要重新导入项目信息:false
     * @param reimportScore   是否要重新导入和计算成绩（仅限网阅项目）:false
     * @param generateReport  是否要生成 Excel 报表文件:false
     * @param exportScore     是否要将成绩导出到阿里云:false
     */
    @RequestMapping(value = "/start/project", method = RequestMethod.POST)
    @ResponseBody
    public Result startAggregation(
            @RequestParam("project") String projectId,
            @RequestParam(value = "type", required = false, defaultValue = "Basic") String type,
            @RequestParam(value = "reimportProject", required = false, defaultValue = "false") String reimportProject,
            @RequestParam(value = "reimportScore", required = false, defaultValue = "false") String reimportScore,
            @RequestParam(value = "generateReport", required = false, defaultValue = "false") String generateReport,
            @RequestParam(value = "exportScore", required = false, defaultValue = "false") String exportScore
    ) {

        //任务进入队列之前，先判断该考试项目是否正在统计
        AggregationStatus aggregationStatus = projectStatusService.getAggregationStatus(projectId);
        if(aggregationStatus.equals(AggregationStatus.Activated)){
            return Result.fail("该项目的统计正在执行中，不能重复执行，请稍后执行!");
        }

        AggregationConfig aggregationConfig = new AggregationConfig();
        aggregationConfig.setAggregationType(AggregationType.valueOf(type));
        aggregationConfig.setReimportProject(Boolean.valueOf(reimportProject));
        aggregationConfig.setReimportScore(Boolean.valueOf(reimportScore));
        aggregationConfig.setGenerateReport(Boolean.valueOf(generateReport));
        aggregationConfig.setExportScore(Boolean.valueOf(exportScore));

        aggregationService.startAggregation(projectId, aggregationConfig);

        return Result.success("项目 " + projectId + " 已经开始统计。");
    }

    @RequestMapping("/project/status")
    @ResponseBody
    public Result getProjectStatus(@RequestParam("project") String projectId) {
        boolean running = aggregationService.isAggregationRunning(projectId);
        Document project = projectService.findProject(projectId);

        String status = (project == null || !project.containsKey("status")) ?
                ProjectStatus.Empty.name() : project.getString("status");

        return Result.success().set("running", running).set("status", status);
    }

    @RequestMapping(value = "/clear/tasks", method = RequestMethod.POST)
    public Result clearRedisQueue() {
        aggregationRoundService.clearTask();
        return Result.success();
    }

    @RequestMapping(value = "/project/cleanData", method = RequestMethod.POST)
    @ResponseBody
    public Result cleanProjectData(@RequestParam("project") String projectId){
        cleanProjectService.doCleanSchedule(projectId);
        return Result.success("项目 " + projectId + " 开始执行清理...");
    }
}
