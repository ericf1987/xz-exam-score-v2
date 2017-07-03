package com.xz.examscore.cache;

import com.hyd.simplecache.SimpleCache;
import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.services.ProjectService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * (description)
 * created at 2017/3/8
 *
 * @author yidin
 */
public class ProjectCacheManagerTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    private ProjectCacheManager projectCacheManager;

    @Autowired
    private ProjectService projectService;

    @Test
    public void testCloseCache() throws Exception {
        String projectId = "11111";
        SimpleCache cache = projectCacheManager.getProjectCache(projectId);
        cache.put("name", "123456");
        System.out.println((String)cache.get("name"));

        projectCacheManager.deleteProjectCache(projectId);
        cache = projectCacheManager.getProjectCache(projectId);
        System.out.println((String)cache.get("name"));
        cache.put("name", "123456");
        System.out.println((String)cache.get("name"));
    }

    @Test
    public void testDeleteProjectCache() throws Exception {
        String projectId = "431100-a1dc056391744ef5afc296541ed4414f";
        projectCacheManager.deleteProjectCache(projectId);
    }

    @Test
    public void testDeleteProjectCache1() throws Exception {
        String projectId = "431100-a1dc056391744ef5afc296541ed4414f";
        String name = projectService.findProject(projectId).getString("name");

        System.out.println(name);
    }
}