package com.xz.examscore.services;

import com.xz.ajiaedu.common.lang.Context;
import com.xz.examscore.asynccomponents.QueueService;
import com.xz.examscore.asynccomponents.aggrtaskdispatcher.TaskDispatcher;
import com.xz.examscore.asynccomponents.aggrtaskdispatcher.TaskDispatcherFactory;
import com.xz.examscore.bean.AggregationConfig;
import com.xz.examscore.bean.AggregationType;
import com.xz.examscore.scanner.ScannerDBService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.xz.examscore.bean.ProjectStatus.*;

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
    ProjectStatusService projectStatusService;

    @Autowired
    ImportProjectService importProjectService;

    @Autowired
    ScannerDBService scannerDBService;

    @Autowired
    ReportService reportService;

    @Autowired
    ExportScoreService exportScoreService;

    @Autowired
    PrepareDataService prepareDataService;

    @Autowired
    ProjectService projectService;

    @Autowired
    QueueService queueService;

    private Set<String> runningProjects = Collections.synchronizedSet(new HashSet<>());

    /**
     * 开始执行项目统计
     *
     * @param projectId 项目ID
     * @param config    选项配置
     */
    public void startAggregation(String projectId, AggregationConfig config, boolean async) {

/*
        if (config.isReimportProject() || config.isReimportScore()) {
            queueService.addToQueue(ImportTaskList, new ImportTaskMessage(
                    projectId, config.isReimportProject(), config.isReimportScore(), true));
        } else {
            queueService.addToQueue(DispatchTaskList, new DispatchTaskMessage(projectId));
        }
*/

        Runnable runnable = () -> {
            try {
                runningProjects.add(projectId);
                projectStatusService.setProjectStatus(projectId, AggregationStarted);

                // 导入考生信息和试题信息
                if (config.isReimportProject()) {
                    importProjectInfo(projectId);
                }

                // 导入成绩信息（网阅）
                if (config.isReimportScore()) {
                    importScannerScore(projectId);
                }

                // 导出成绩到阿里云
                if (config.isExportScore()) {
                    exportScore(projectId);
                }

                // 导入学生科目信息
                prepareDataService.prepare(projectId);

                // 统计成绩
                try {
                    runAggregation0(projectId, config);
                } finally {
                    //更新统计时间到project_list表
                    projectService.updateAggregationTime(projectId);
                    projectStatusService.setProjectStatus(projectId, AggregationCompleted);
                }

                // 生成报表
                if (config.isGenerateReport()) {
                    generateReports(projectId);
                }

            } catch (Exception e) {
                projectStatusService.setProjectStatus(projectId, AggregationFailed);
                LOG.error("执行统计失败", e);
            } finally {
                runningProjects.remove(projectId);
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

    private void exportScore(String projectId) {
        Runnable runnable = () -> {
            try {
                exportScoreService.exportScore(projectId, true);
            } catch (Exception e) {
                LOG.error("导出成绩失败", e);
            }
        };

        new Thread(runnable).start();
    }

    private void generateReports(String projectId) {
        projectStatusService.setProjectStatus(projectId, ReportGenerating);
        reportService.generateReports(projectId, false);
        projectStatusService.setProjectStatus(projectId, ReportGenerated);
    }

    private void importScannerScore(String projectId) {
        projectStatusService.setProjectStatus(projectId, ScoreImporting);
        scannerDBService.importProjectScore(projectId);
        projectStatusService.setProjectStatus(projectId, ScoreImported);
    }

    private void importProjectInfo(String projectId) {
        projectStatusService.setProjectStatus(projectId, ProjectImporting);
        importProjectService.importProject(projectId, true);
        projectStatusService.setProjectStatus(projectId, ProjectImported);
    }

    private void runAggregation0(String projectId, AggregationConfig aggregationConfig) {
        String aggregationId = UUID.randomUUID().toString();
        LOG.info("----开始对项目{}的统计，本次统计ID={}", projectId, aggregationId);

        List<TaskDispatcher> dispatcherList;
        int round = 1;

        do {
            dispatcherList = createDispatchers(aggregationId, aggregationConfig.getAggregationType());
            LOG.info("----对项目{}的第{}轮统计(ID={})任务：{}", projectId, round, aggregationId, dispatcherList);

            runDispatchers(projectId, aggregationId, dispatcherList);
            LOG.info("----对项目{}的第{}轮统计(ID={})任务分发完毕", projectId, round, aggregationId);

            waitForTaskCompletion(aggregationId);
            LOG.info("----对项目{}的第{}轮统计(ID={})任务执行完毕。", projectId, round, aggregationId);

            round += 1;
        } while (!dispatcherList.isEmpty());

        LOG.info("====对项目{}的统计全部结束，本次统计ID={}", projectId, aggregationId);
    }

    // 等待本轮统计
    private void waitForTaskCompletion(String aggregationId) {
        aggregationRoundService.waitForRoundCompletion(aggregationId);
    }

    // 查询本轮统计用到的 Dispatcher 对象列表
    private List<TaskDispatcher> createDispatchers(String aggregationId, AggregationType aggregationType) {
        return taskDispatcherFactory.listAvailableDispatchers(aggregationId, aggregationType);
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
