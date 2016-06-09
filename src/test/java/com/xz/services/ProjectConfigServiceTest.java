package com.xz.services;

import com.alibaba.fastjson.JSON;
import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.ajiaedu.common.report.Keys.ScoreLevel;
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
        // saveDefaultConfig();
        // saveShifengConfig();
        // save19SchoolConfig();
        saveShifeng2Config();
    }

    // 缺省配置
    private void saveDefaultConfig() {
        ProjectConfig config = new ProjectConfig();
        config.setProjectId("[default]");
        config.setCombineCategorySubjects(false);

        config.addScoreLevel(ScoreLevel.Excellent.name(), 0.9);
        config.addScoreLevel(ScoreLevel.Good.name(), 0.8);
        config.addScoreLevel(ScoreLevel.Pass.name(), 0.6);
        config.addScoreLevel(ScoreLevel.Fail.name(), 0.0);

        config.addRankingLevel("A", 0.20);
        config.addRankingLevel("B", 0.20);
        config.addRankingLevel("C", 0.20);
        config.addRankingLevel("D", 0.20);
        config.addRankingLevel("E", 0.20);

        projectConfigService.saveProjectConfig(config);
    }

    // 石峰中学配置（2016-05）
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

    // 石峰中学配置（2016-06）
    private void saveShifeng2Config() {
        ProjectConfig config = new ProjectConfig();
        config.setProjectId("430200-3626a0b5e6ed4ec2b4a5f2bbd6647ef3");
        config.setCombineCategorySubjects(true);
        config.addRankingLevel("A", 0.40);
        config.addRankingLevel("B", 0.25);
        config.addRankingLevel("C", 0.23);
        config.addRankingLevel("D", 0.07);
        config.addRankingLevel("E", 0.04);
        config.addRankingLevel("F", 0.01);

        projectConfigService.saveProjectConfig(config);
    }

    // 石峰中学配置
    private void save19SchoolConfig() {
        ProjectConfig config = new ProjectConfig();
        config.setProjectId("430200-89c9dc7481cd47a69d85af3f0808e0c4");
        config.setCombineCategorySubjects(false);
        config.addRankingLevel("A", 0.12);
        config.addRankingLevel("B", 0.20);
        config.addRankingLevel("C", 0.25);
        config.addRankingLevel("D", 0.25);
        config.addRankingLevel("E", 0.16);
        config.addRankingLevel("F", 0.02);

        projectConfigService.saveProjectConfig(config);
    }

    @Test
    public void testGetProjectConfig() throws Exception {
        String projectId = "430200-89c9dc7481cd47a69d85af3f0808e0c4";
        ProjectConfig projectConfig = projectConfigService.getProjectConfig(projectId);
        System.out.println(JSON.toJSONString(projectConfig));
    }
}