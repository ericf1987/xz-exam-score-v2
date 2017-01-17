package com.xz.examscore.cache;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "cache.project")
public class CacheConfig {

    private int entryTtl;

    private int projectTtl;

    private int maxEntry;

    public int getProjectTtl() {
        return projectTtl;
    }

    public void setProjectTtl(int projectTtl) {
        this.projectTtl = projectTtl;
    }

    public int getEntryTtl() {
        return entryTtl;
    }

    public void setEntryTtl(int entryTtl) {
        this.entryTtl = entryTtl;
    }

    public int getMaxEntry() {
        return maxEntry;
    }

    public void setMaxEntry(int maxEntry) {
        this.maxEntry = maxEntry;
    }
}
