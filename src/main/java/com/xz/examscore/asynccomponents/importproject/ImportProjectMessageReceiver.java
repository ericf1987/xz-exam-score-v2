package com.xz.examscore.asynccomponents.importproject;

import com.xz.ajiaedu.common.concurrent.Executors;
import com.xz.examscore.asynccomponents.QueueService;
import com.xz.examscore.asynccomponents.aggrtaskdispatcher.DispatchTaskMessage;
import com.xz.examscore.scanner.ScannerDBService;
import com.xz.examscore.services.ImportProjectService;
import com.xz.examscore.services.ProjectStatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ThreadPoolExecutor;

import static com.xz.examscore.asynccomponents.QueueType.DispatchTaskList;
import static com.xz.examscore.asynccomponents.QueueType.ImportTaskList;
import static com.xz.examscore.bean.ProjectStatus.*;

/**
 * 自动拉取并执行项目导入任务消息
 *
 * @author yiding_he
 */
@Component
public class ImportProjectMessageReceiver {

    static final Logger LOG = LoggerFactory.getLogger(ImportProjectMessageReceiver.class);

    @Value("${task.executor.poolsize}")
    int executorPoolSize;

    @Autowired
    QueueService queueService;

    @Autowired
    ImportProjectService importProjectService;

    @Autowired
    ProjectStatusService projectStatusService;

    @Autowired
    ScannerDBService scannerDBService;

    private ThreadPoolExecutor executorService;

    @PostConstruct
    public void init() {

        executorService = Executors.newBlockingThreadPoolExecutor(1, executorPoolSize, 1);

        Runnable runnable = () -> {
            while (true) {

                while (executorService.getActiveCount() >= executorPoolSize) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        // nothing to do
                    }
                }

                ImportTaskMessage message = queueService.readFromQueue(ImportTaskList, 3);
                executeTask(message);
            }
        };

        Thread thread = new Thread(runnable);
        thread.setDaemon(true);
        thread.start();
    }

    private void executeTask(final ImportTaskMessage message) {
        executorService.submit(() -> {
            String projectId = message.getProjectId();
            LOG.info("开始导入项目 " + projectId);

            try {
                // 导入项目基本信息
                if (message.isImportProjectInfo()) {
                    projectStatusService.setProjectStatus(projectId, ProjectImporting);
                    importProjectService.importProject(projectId, true);
                    projectStatusService.setProjectStatus(projectId, ProjectImported);
                }

                // 导入网阅成绩信息
                if (message.isImportProjectScore()) {
                    projectStatusService.setProjectStatus(projectId, ScoreImporting);
                    scannerDBService.importProjectScore(projectId);
                    projectStatusService.setProjectStatus(projectId, ScoreImported);
                }

                // 如果需要，则发送开始统计命令
                if (message.isInAggrProcess()) {
                    queueService.addToQueue(DispatchTaskList, new DispatchTaskMessage(projectId));
                }
            } catch (Exception e) {
                LOG.error("导入项目失败", e);
                projectStatusService.setProjectStatus(projectId, AggregationFailed);
            }
        });
    }
}
