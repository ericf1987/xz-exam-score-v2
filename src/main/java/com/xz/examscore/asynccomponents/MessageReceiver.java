package com.xz.examscore.asynccomponents;

import com.xz.ajiaedu.common.concurrent.Executors;
import com.xz.ajiaedu.common.lang.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.ThreadPoolExecutor;

import static com.xz.examscore.asynccomponents.QueueType.ImportTaskList;

/**
 * 自动拉取并执行任务
 *
 * @author yiding_he
 */
public abstract class MessageReceiver<T extends QueueMessage> {

    private static final Logger LOG = LoggerFactory.getLogger(MessageReceiver.class);

    private static final String SERVER_TYPE_ARG = System.getProperty("serverType");

    static {
        LOG.info("服务器类型: " + SERVER_TYPE_ARG);
    }

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

        // 检查当前类型的队列消息是否可接受
        if (!isAcceptable()) {
            return;
        }

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

    private boolean isAcceptable() {

        if (StringUtil.isEmpty(SERVER_TYPE_ARG)) {
            return false;
        }

        Type genericSuperclass = getClass().getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType) {

            String messageTypeName = getAcceptableMessageTypeName((ParameterizedType) genericSuperclass);

            if (isAcceptable(messageTypeName, SERVER_TYPE_ARG)) {
                return true;
            }
        }

        return false;
    }

    // 检查服务器的可接受消息类型与当前 MessageReceiver 对象是否一致
    private boolean isAcceptable(String messageTypeName, String serverTypeArg) {
        if (serverTypeArg != null) {
            String[] serverTypeArgs = serverTypeArg.split(",");

            for (String type : serverTypeArgs) {
                if (messageTypeName.startsWith(type)) {
                    return true;
                }
            }
        }
        return false;
    }

    // 获得当前 MessageReceiver 对象可接受的 QueueMessage 类型
    private String getAcceptableMessageTypeName(ParameterizedType genericSuperclass) {
        Type messageType = genericSuperclass.getActualTypeArguments()[0];
        return ((Class) messageType).getSimpleName().toLowerCase();
    }

    protected abstract void executeTask(T message);
}
