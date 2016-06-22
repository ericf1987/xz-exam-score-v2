package com.xz.services;

import com.xz.ajiaedu.common.lang.Context;
import com.xz.bean.ProjectStatus;
import com.xz.scanner.ScannerDBService;
import com.xz.taskdispatchers.TaskDispatcher;
import com.xz.taskdispatchers.TaskDispatcherFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.xz.bean.ProjectStatus.*;

/**
 * 处理统计任务的进度和轮次安排
 *
 * @author yiding_he
 */
@Service
public class AggregationService {

    static final Logger LOG = LoggerFactory.getLogger(AggregationService.class);

    @Autowired
    TaskDispatcherFactory taskDispatcherFactory;

    @Autowired
    AggregationRoundService aggregationRoundService;

    @Autowired
    ProjectConfigService projectConfigService;

    @Autowired
    PrepareDataService prepareDataService;

    @Autowired
    ProjectStatusService projectStatusService;

    @Autowired
    ImportProjectService importProjectService;

    @Autowired
    ScannerDBService scannerDBService;

    @Autowired
    ReportService reportService;

    private Set<String> runningProjects = Collections.synchronizedSet(new HashSet<>());

    /**
     * 开始对项目执行统计
     *
     * @param projectId        项目ID
     * @param async            是否另起线程执行
     * @param dataReady        项目数据是否已经准备好，只需要统计。如果为 false，则尝试重新导入整个项目数据
     * @param isGenerateReport 是否要生成报表文件
     */
    public void startAggregation(
            String projectId, boolean async, boolean dataReady, Boolean isGenerateReport) {

        Runnable runnable = () -> {
            try {
                runningProjects.add(projectId);
                projectStatusService.setProjectStatus(projectId, AggregationStarted);

                String aggregationId = UUID.randomUUID().toString();

                // 数据导入
                if (!dataReady) {
                    beforeAggregation(projectId, aggregationId);
                }

                // 统计成绩
                runAggregation0(projectId, dataReady);

                // 生成报表
                if (isGenerateReport) {
                    projectStatusService.setProjectStatus(projectId, ReportGenerating);
                    reportService.generateReports(projectId);
                    projectStatusService.setProjectStatus(projectId, ReportGenerated);
                }

            } catch (Exception e) {
                projectStatusService.setProjectStatus(projectId, AggregationFailed);
                LOG.error("执行统计失败", e);
            } finally {
                runningProjects.remove(projectId);
                projectStatusService.setProjectStatus(projectId, AggregationCompleted);
            }
        };

        if (async) {
            Thread thread = new Thread(runnable);
            thread.setDaemon(true);
            thread.start();
        } else {
            runnable.run();
        }
    }

    private void runAggregation0(String projectId, boolean dataReady) {
        String aggregationId = UUID.randomUUID().toString();
        LOG.info("----开始对项目{}的统计，本次统计ID={}", projectId, aggregationId);

        if (!dataReady) {
            beforeAggregation(projectId, aggregationId);
        }

        List<TaskDispatcher> dispatcherList;
        int round = 1;

        do {
            dispatcherList = createDispatchers(aggregationId);
            LOG.info("----对项目{}的第{}轮统计(ID={})任务：{}", projectId, round, aggregationId, dispatcherList);

            runDispatchers(projectId, aggregationId, dispatcherList);
            LOG.info("----对项目{}的第{}轮统计(ID={})任务分发完毕", projectId, round, aggregationId);

            waitForTaskCompletion(aggregationId);
            LOG.info("----对项目{}的第{}轮统计(ID={})任务执行完毕。", projectId, round, aggregationId);

            round += 1;
        } while (!dispatcherList.isEmpty());

        LOG.info("====对项目{}的统计全部结束，本次统计ID={}", projectId, aggregationId);
    }

    private void beforeAggregation(String projectId, String aggregationId) {

        ProjectStatus projectStatus = projectStatusService.getProjectStatus(projectId);

        if (projectStatus == ProjectStatus.Empty) {
            LOG.info("----开始导入项目{}", projectId);
            projectStatusService.setProjectStatus(projectId, ProjectImporting);
            importProjectService.importProject(projectId);
            projectStatusService.setProjectStatus(projectId, ProjectImported);
        }

        projectStatusService.setProjectStatus(projectId, ScoreImporting);
        scannerDBService.importProjectScore(projectId);
        projectStatusService.setProjectStatus(projectId, ScoreImported);

        LOG.info("----对项目{}准备开始统计(ID={})", projectId, aggregationId);
        prepareDataService.prepare(projectId);
    }

    // 等待本轮统计
    private void waitForTaskCompletion(String aggregationId) {
        aggregationRoundService.waitForRoundCompletion(aggregationId);
    }

    // 查询本轮统计用到的 Dispatcher 对象列表
    private List<TaskDispatcher> createDispatchers(String aggregationId) {
        return taskDispatcherFactory.listAvailableDispatchers(aggregationId);
    }

    /**
     * 执行 Dispatcher 列表
     *
     * @param projectId      项目ID
     * @param aggregationId  本次统计ID
     * @param dispatcherList Dispatcher 列表
     */
    public void runDispatchers(String projectId, String aggregationId, List<TaskDispatcher> dispatcherList) {
        for (TaskDispatcher dispatcher : dispatcherList) {
            Context context = new Context();
            context.put("projectId", projectId);
            context.put("aggregationId", aggregationId);
            context.put("projectConfig", projectConfigService.getProjectConfig(projectId));
            dispatcher.dispatch(context);
        }
    }

    public boolean isAggregationRunning(String projectId) {
        return runningProjects.contains(projectId);
    }
}
