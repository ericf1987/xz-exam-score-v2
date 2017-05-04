package com.xz.examscore.cache;

import com.hyd.simplecache.EhCacheConfiguration;
import com.hyd.simplecache.SimpleCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class ProjectCacheManager {

    @Autowired
    private CacheConfig cacheConfig;

    private long lastShrink;

    private final Map<String, CacheWrapper> projectCacheMap = new ConcurrentHashMap<>();

    /**
     * 获得一个项目缓存实例。如果缓存实例不存在，则自动创建一个
     *
     * @param projectId 项目ID
     *
     * @return 项目缓存实例
     */
    public SimpleCache getProjectCache(String projectId) {

        if (!projectCacheMap.containsKey(projectId)) {
            synchronized (projectCacheMap) {
                if (!projectCacheMap.containsKey(projectId)) {
                    projectCacheMap.put(projectId, createCache(projectId));
                }
            }
        }

        shrink();

        return projectCacheMap.get(projectId).simpleCache;
    }

    private void shrink() {
        long now = System.currentTimeMillis();

        if (now - lastShrink > TimeUnit.MINUTES.toMillis(1)) {
            projectCacheMap.entrySet().removeIf(entry -> entry.getValue().isExpired());
            lastShrink = now;
        }
    }

    private CacheWrapper createCache(String projectId) {
        EhCacheConfiguration configuration = new EhCacheConfiguration();
        configuration.setName(projectId);
        configuration.setMaxEntriesLocalHeap(cacheConfig.getMaxEntry());
        configuration.setTimeToLiveSeconds(cacheConfig.getEntryTtl());
        return new CacheWrapper(new SimpleCache(configuration));
    }

    /**
     * 删除项目缓存实例
     *
     * @param projectId 项目ID
     */
    public void deleteProjectCache(String projectId) {
        if (projectCacheMap.containsKey(projectId)) {
            projectCacheMap.get(projectId).simpleCache.close();
            projectCacheMap.remove(projectId);
        }
    }

    //////////////////////////////////////////////////////////////

    private class CacheWrapper {

        public SimpleCache simpleCache;

        public long lastAccess;

        public CacheWrapper(SimpleCache simpleCache) {
            this.simpleCache = simpleCache;
            this.lastAccess = System.currentTimeMillis();
        }

        public boolean isExpired() {
            return System.currentTimeMillis() - lastAccess > cacheConfig.getProjectTtl() * 1000;
        }
    }
}
