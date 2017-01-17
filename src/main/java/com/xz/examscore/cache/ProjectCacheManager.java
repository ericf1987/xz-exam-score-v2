package com.xz.examscore.cache;

import com.hyd.simplecache.EhCacheConfiguration;
import com.hyd.simplecache.SimpleCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ProjectCacheManager {

    @Autowired
    CacheConfig cacheConfig;

    private Map<String, SimpleCache> projectCacheMap = new HashMap<>();

    private Map<String, Long> projectCacheLastAccess = new HashMap<>();

    /**
     * 获得一个项目缓存实例。如果缓存实例不存在，则自动创建一个
     *
     * @param projectId 项目ID
     *
     * @return 项目缓存实例
     */
    public synchronized SimpleCache getProjectCache(String projectId) {
        if (!projectCacheMap.containsKey(projectId)) {
            projectCacheMap.put(projectId, createCache(projectId));
        }

        long now = System.currentTimeMillis();
        projectCacheLastAccess.put(projectId, now);

        // 删除长时间未访问的项目缓存
        projectCacheLastAccess.entrySet().removeIf(
                entry -> now - entry.getValue() > cacheConfig.getProjectTtl() * 1000);

        return projectCacheMap.get(projectId);
    }

    private SimpleCache createCache(String projectId) {
        EhCacheConfiguration configuration = new EhCacheConfiguration();
        configuration.setName(projectId);
        configuration.setMaxEntriesLocalHeap(cacheConfig.getMaxEntry());
        configuration.setTimeToLiveSeconds(cacheConfig.getEntryTtl());
        return new SimpleCache(configuration);
    }

    /**
     * 删除项目缓存实例
     *
     * @param projectId 项目ID
     */
    public synchronized void deleteProjectCache(String projectId) {
        projectCacheMap.remove(projectId);
        projectCacheLastAccess.remove(projectId);
    }
}
