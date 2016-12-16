package com.xz.examscore.services;

import com.xz.ajiaedu.common.lang.Context;
import com.xz.examscore.AppException;
import com.xz.examscore.asynccomponents.QueueService;
import com.xz.examscore.asynccomponents.aggrtaskdispatcher.DispatchTaskMessage;
import com.xz.examscore.asynccomponents.aggrtaskdispatcher.TaskDispatcher;
import com.xz.examscore.asynccomponents.aggrtaskdispatcher.TaskDispatcherFactory;
import com.xz.examscore.asynccomponents.importproject.ImportTaskMessage;
import com.xz.examscore.bean.AggregationConfig;
import com.xz.examscore.bean.AggregationStatus;
import com.xz.examscore.bean.AggregationType;
import com.xz.examscore.bean.Range;
import com.xz.examscore.scanner.ScannerDBService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.xz.examscore.asynccomponents.QueueType.DispatchTaskList;
import static com.xz.examscore.asynccomponents.QueueType.ImportTaskList;
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

    @Autowired
    RangeService rangeService;

    @Autowired
    RecordExceptionService recordExceptionService;

    private Set<String> runningProjects = Collections.synchronizedSet(new HashSet<>());

    /**
     * 开始执行项目统计
     *
     * @param projectId 项目ID
     * @param config    选项配置
     */
    public void startAggregation(String projectId, AggregationConfig config) {

        boolean reimportProject = config.isReimportProject();
        boolean reimportScore = config.isReimportScore();
        boolean generateReport = config.isGenerateReport();
        boolean exportScore = config.isExportScore();
        AggregationType aggregationType = config.getAggregationType();

        recordExceptionService.deleteExceptionRecord(projectId, null);

        if (reimportProject || reimportScore) {
            ImportTaskMessage message = new ImportTaskMessage(projectId, reimportProject, reimportScore, exportScore);
            message.setAggregationType(aggregationType);
            message.setGenerateReport(generateReport);
            message.setExportScore(exportScore);
            queueService.addToQueue(ImportTaskList, message);
        } else {
            queueService.addToQueue(DispatchTaskList,
                    new DispatchTaskMessage(projectId, aggregationType, generateReport, exportScore));
        }
    }

    public void runAggregationOnly(String projectId, AggregationType aggregationType) {
        try {
            projectStatusService.setAggregationStatus(projectId, AggregationStatus.Activated);
            prepareDataService.prepare(projectId);
            projectStatusService.setProjectStatus(projectId, AggregationStarted);
            runAggregation0(projectId, aggregationType);
            projectService.updateAggregationTime(projectId);
            projectStatusService.setProjectStatus(projectId, AggregationCompleted);
            projectStatusService.setAggregationStatus(projectId, AggregationStatus.Terminated);
        } catch (AppException e) {
            projectService.updateAggregationTime(projectId);
            projectStatusService.setProjectStatus(projectId, AggregationFailed);
            projectStatusService.setAggregationStatus(projectId, AggregationStatus.Terminated);
            recordExceptionService.recordException(projectId, AggregationFailed, e);
            throw e;
        } catch (Exception e) {
            projectService.updateAggregationTime(projectId);
            projectStatusService.setProjectStatus(projectId, AggregationFailed);
            projectStatusService.setAggregationStatus(projectId, AggregationStatus.Terminated);
            recordExceptionService.recordException(projectId, AggregationFailed, e);
            throw new AppException(e);
        }
    }

    public void exportScore(String projectId) {
        Runnable runnable = () -> {
            try {
                LOG.info("开始导出项目到阿里云：" + projectId);
                exportScoreService.exportScore(projectId, true);
                LOG.info("导出成功：" + projectId);
            } catch (Exception e) {
                LOG.error("导出成绩失败", e);
            }
        };
        new Thread(runnable).start();
    }

    public void generateReports(String projectId) {
        projectStatusService.setProjectStatus(projectId, ReportGenerating);
        reportService.generateReports(projectId, false);
        projectStatusService.setProjectStatus(projectId, ReportGenerated);
    }

    public void importScannerScore(String projectId) {
        projectStatusService.setProjectStatus(projectId, ScoreImporting);
        scannerDBService.importProjectScore(projectId);
        projectStatusService.setProjectStatus(projectId, ScoreImported);

    }

    public void importProjectInfo(String projectId) {
        projectStatusService.setProjectStatus(projectId, ProjectImporting);
        importProjectService.importProject(projectId, true);
        projectStatusService.setProjectStatus(projectId, ProjectImported);
    }

    private void runAggregation0(String projectId, AggregationType aggregationType) {
        String aggregationId = UUID.randomUUID().toString();
        LOG.info("----开始对项目{}的统计，本次统计ID={}", projectId, aggregationId);

        LOG.info("----开始查询项目{}的维度信息----");
        long begin = System.currentTimeMillis();
        Map<String, List<Range>> rangesMap = rangeService.getRangesMap(projectId);
        long end = System.currentTimeMillis();
        LOG.info("----统计维度列表耗时：{}", end - begin);
        LOG.info("----项目{}的维度信息查询结束----", projectId);

        List<TaskDispatcher> dispatcherList;
        int round = 1;

        do {
            dispatcherList = createDispatchers(aggregationId, aggregationType);
            List<String> dispatcherListNames = dispatcherList.stream()
                    .map(d -> d.getClass().getSimpleName())
                    .collect(Collectors.toList());

            LOG.info("----对项目{}的第{}轮统计(ID={})任务：{}", projectId, round, aggregationId, dispatcherListNames);

            runDispatchers(projectId, aggregationId, dispatcherList, rangesMap);
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
     * @param rangesMap       维度列表
     */
    public void runDispatchers(String projectId, String aggregationId, List<TaskDispatcher> dispatcherList, Map<String, List<Range>> rangesMap) {
        for (TaskDispatcher dispatcher : dispatcherList) {
            Context context = new Context();
            context.put("projectId", projectId);
            context.put("aggregationId", aggregationId);
            context.put("projectConfig", projectConfigService.getProjectConfig(projectId));
            context.put("rangesMap", rangesMap);
            dispatcher.dispatch(context);
        }
    }

    public boolean isAggregationRunning(String projectId) {
        return runningProjects.contains(projectId);
    }
}
