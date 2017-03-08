package com.xz.examscore.cache;

import com.hyd.simplecache.SimpleCache;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2017/3/8.
 */
public class ProjectCacheManagerTest extends XzExamScoreV2ApplicationTests{

    @Autowired
    ProjectCacheManager projectCacheManager;

    @Test
    public void testGetProjectCache() throws Exception {
        String projectId = "22222";
        SimpleCache simpleCache = projectCacheManager.getProjectCache(projectId);
        simpleCache.put("name", "eric");
        System.out.println(simpleCache.get("name").toString());

        projectCacheManager.deleteProjectCache(projectId);
        System.out.println(simpleCache.get("name").toString());
    }

    @Test
    public void testDeleteProjectCache() throws Exception {

    }
}