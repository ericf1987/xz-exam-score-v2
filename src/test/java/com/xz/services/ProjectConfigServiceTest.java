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

    public static final String[] LEVELS = {"A", "B", "C", "D", "E", "F"};

    @Autowired
    ProjectConfigService projectConfigService;

    @Test
    public void testSaveProjectConfig() throws Exception {
        saveConfig("430200-e1274973fe994a86a9552a168fdeaa01", false, new double[]{0.40, 0.25, 0.23, 0.07, 0.04, 0.01});
        saveConfig("430200-b73f03af1d74484f84f1aa93f583caaa", false, new double[]{0.40, 0.25, 0.23, 0.07, 0.04, 0.01});
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

    /**
     * 保存配置
     *
     * @param projectId               项目ID
     * @param combineCategorySubjects 是否合并文理科
     * @param levelPercentages        等第分布，从 A 开始算起，最多 6 个等第
     */
    private void saveConfig(String projectId, boolean combineCategorySubjects, double[] levelPercentages) {
        ProjectConfig config = new ProjectConfig();
        config.setProjectId(projectId);
        config.setCombineCategorySubjects(combineCategorySubjects);

        for (int i = 0; i < LEVELS.length; i++) {
            String level = LEVELS[i];
            if (levelPercentages.length > i) {
                config.addRankingLevel(level, levelPercentages[i]);
            }
        }

        projectConfigService.saveProjectConfig(config);
    }

    @Test
    public void testGetProjectConfig() throws Exception {
        String projectId = "430200-89c9dc7481cd47a69d85af3f0808e0c4";
        ProjectConfig projectConfig = projectConfigService.getProjectConfig(projectId);
        System.out.println(JSON.toJSONString(projectConfig));
    }
}