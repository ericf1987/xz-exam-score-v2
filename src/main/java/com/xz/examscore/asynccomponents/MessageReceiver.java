package com.xz.examscore.asynccomponents;

import com.xz.ajiaedu.common.concurrent.Executors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.util.concurrent.ThreadPoolExecutor;

import static com.xz.examscore.asynccomponents.QueueType.ImportTaskList;

/**
 * 自动拉取并执行任务
 *
 * @author yiding_he
 */
public abstract class MessageReceiver<T extends QueueMessage> {

    @Value("${task.executor.poolsize}")
    int executorPoolSize;

    @Autowired
    QueueService queueService;

    private ThreadPoolExecutor executorService;

    protected QueueService getQueueService() {
        return queueService;
    }

    @SuppressWarnings("InfiniteLoopStatement")
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

                T message = queueService.readFromQueue(ImportTaskList, 3);
                executorService.submit(() -> executeTask(message));
            }
        };

        Thread thread = new Thread(runnable);
        thread.setDaemon(true);
        thread.start();
    }

    protected abstract void executeTask(T message);
}
