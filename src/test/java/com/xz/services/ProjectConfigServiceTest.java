package com.xz.services;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.bean.ProjectConfig;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
        saveShifengConfig();
    }

    // 石峰中学配置
    private void saveShifengConfig() {
        ProjectConfig config = new ProjectConfig();
        config.setProjectId("430200-8a9be9fc2e1842a4b9b4894eee1f5f73");
        config.setCombineCategorySubjects(true);
        config.addRankingLevel("A", 0.40);
        config.addRankingLevel("B", 0.25);
        config.addRankingLevel("C", 0.23);
        config.addRankingLevel("D", 0.07);
        config.addRankingLevel("E", 0.04);
        config.addRankingLevel("F", 0.01);

        projectConfigService.saveProjectConfig(config);
    }
}