package com.xz.examscore.asynccomponents;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "task.executor")
public class ExecutorConfig {

    private Map<String, Integer> poolsize = new HashMap<>();

    public Map<String, Integer> getPoolsize() {
        return poolsize;
    }

    public void setPoolsize(Map<String, Integer> poolsize) {
        this.poolsize = poolsize;
    }

    public int getPoolSize(Class<? extends QueueMessage> messageType) {
        String messageTypeName = messageType.getSimpleName().toLowerCase();

        for (Map.Entry<String, Integer> entry : poolsize.entrySet()) {
            if (messageTypeName.startsWith(entry.getKey())) {
                return entry.getValue();
            }
        }

        return 0;
    }
}
