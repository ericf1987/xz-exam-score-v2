package com.xz.examscore.services;

import com.xz.ajiaedu.common.report.Keys.ScoreLevel;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.bean.ProjectConfig;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Autowired
    StudentService studentService;

    @Test
    public void testSaveProjectConfig() throws Exception {
        saveConfig("431100-f1e329d8a05f4ba7b7398aec7976af58", false, null, new double[]{0.8, 0.7, 0.6, 0.0});
        saveConfig("431100-aa38e94f9c664e4f88d4e94494f2d962", false, null, new double[]{0.8, 0.7, 0.6, 0.0});
        saveConfig("431100-da3ee7b880424a90afc544f62bef2f65", false, null, new double[]{0.8, 0.7, 0.6, 0.0});
        saveConfig("431100-e8825df7825441bf98282dddc2ad3cd3", false, null, new double[]{0.8, 0.7, 0.6, 0.0});
        saveConfig("431100-1e777384b71a4f11b57ebda0798f750e", false, null, new double[]{0.8, 0.7, 0.6, 0.0});
        saveConfig("431100-b25c3bc8617b480da22278e74f2855e4", false, null, new double[]{0.8, 0.7, 0.6, 0.0});
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

        projectConfigService.replaceProjectConfig(config);
    }

    /**
     * 保存配置
     *
     * @param projectId               项目ID
     * @param combineCategorySubjects 是否合并文理科
     * @param levelPercentages        等第分布，从 A 开始算起，最多 6 个等第
     * @param scoreRates              四率（优秀/良好/及格/不及格）得分率
     */
    private void saveConfig(
            String projectId,
            boolean combineCategorySubjects,
            double[] levelPercentages,
            double[] scoreRates
    ) {

        ProjectConfig config = new ProjectConfig();
        config.setProjectId(projectId);
        config.setCombineCategorySubjects(combineCategorySubjects);

        if (levelPercentages != null && levelPercentages.length > 0) {
            for (int i = 0; i < LEVELS.length; i++) {
                String level = LEVELS[i];
                if (levelPercentages.length > i) {
                    config.addRankingLevel(level, levelPercentages[i]);
                }
            }
        }

        if (scoreRates != null && scoreRates.length > 0) {
            HashMap<String, Object> scoreLevels = new HashMap<>();
            scoreLevels.put(ScoreLevel.Excellent.name(), scoreRates[0]);
            scoreLevels.put(ScoreLevel.Good.name(), scoreRates[1]);
            scoreLevels.put(ScoreLevel.Pass.name(), scoreRates[2]);
            scoreLevels.put(ScoreLevel.Fail.name(), scoreRates[3]);
            config.setScoreLevels(scoreLevels);
        }

        projectConfigService.replaceProjectConfig(config);
    }

    @Test
    public void testMergeProjectConfig() throws Exception {
        ProjectConfig config = new ProjectConfig();
        config.setProjectId("430100-eddd6548432c4e5fab54739101c0affb");
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

        config.setRankSegmentCount(20);
        projectConfigService.replaceProjectConfig(config);
    }

    @Test
    public void testGetProjectConfig() throws Exception {
        ProjectConfig projectConfig = projectConfigService.getProjectConfig("430100-405f6648c79545d589687a1d0763fc9f");
        System.out.println(projectConfig.getTopStudentRate());
        System.out.println(projectConfig.getRankLevels());
        System.out.println(projectConfig.getRankLevelCombines().toString());
    }

    @Test
    public void getEntryLevelMap() throws Exception {
        String projectId = "430100-1944e9f7048b48e2b38e35db75be4980";
        Range range = Range.province("430000");
        Target projectTarget = Target.project(projectId);
        int count = studentService.getStudentCount(projectId, range, projectTarget);
        List<Double> list = projectConfigService.getEntryLevelScoreLine(projectId, range, projectTarget, count);
        System.out.println(list);
    }

    @Test
    public void getScoreByIndex() throws Exception {
        String projectId = "433100-fef19389d6ce4b1f99847ab96d2cfeba";
        Range range = Range.school("64a1c8cd-a9b9-4755-a973-e1ce07f3f70a");
        Target target = Target.project(projectId);
        double scoreByIndex = projectConfigService.getScoreByIndex(projectId, range, target, 5, "0.4");
        System.out.println(scoreByIndex);
    }

    @Test
    public void testgetScoreLevelByConfig() throws Exception {
        String projectId = "430100-354dce3ac8ef4800a1b57f81a10b8baa";
        Target subjectTarget = Target.subject("004");
        ProjectConfig projectConfig = projectConfigService.getProjectConfig(projectId);
        Map<String, Object> scoreLevelByConfig = projectConfigService.getScoreLevelByConfig(subjectTarget, projectConfig);
        System.out.println(scoreLevelByConfig);
    }
}