package com.xz.services;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.bean.ProjectConfig;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * (description)
 * created at 16/05/16
 *
 * @author yiding_he
 */
public class ProjectConfigServiceTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    ProjectConfigService projectConfigService;

    @Test
    public void testSaveProjectConfig() throws Exception {
        ProjectConfig config = new ProjectConfig();
        config.setProjectId("[default]");
        config.setCombineCategorySubjects(false);
        config.addRankingLevel("A", 0.2);
        config.addRankingLevel("B", 0.2);
        config.addRankingLevel("C", 0.2);
        config.addRankingLevel("D", 0.2);
        config.addRankingLevel("E", 0.2);

        projectConfigService.saveProjectConfig(config);
    }
}