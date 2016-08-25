package com.xz.examscore.bean;

import java.io.Serializable;
import java.util.*;

/**
 * 报表的项目个性化配置
 *
 * @author yiding_he
 */
public class ProjectConfig implements Serializable {

    /**
     * 项目ID
     */
    private String projectId;

    /**
     * 对于没有文综理综的考试，是否额外组合文综科目和理综科目
     */
    private boolean combineCategorySubjects = false;

    /**
     * 排名等级配置，每个排名等级占整个排名的多少比例，加起来为 1
     */
    private Map<String, Double> rankLevels = new HashMap<>();

    /**
     * 排名分段的数量
     */
    private int rankSegmentCount;

    /**
     * 得分等级配置，每个等级的得分率
     */
    private Map<String, Double> scoreLevels = new HashMap<>();

    /**
     * 选择展示哪些等第组合（如 5A、4A1B）
     */
    private List<String> rankLevelCombines = new ArrayList<>();

    /**
     * 考试开始日期，格式 yyyy-MM-dd
     */
    private String startDate;

    /**
     * 尖子生比率
     */
    private double topStudentRate;

    /**
     * 高分段比率
     */
    private double highScoreRate;

    public double getHighScoreRate() {
        return highScoreRate;
    }

    public void setHighScoreRate(double highScoreRate) {
        this.highScoreRate = highScoreRate;
    }

    public double getTopStudentRate() {
        return topStudentRate;
    }

    public void setTopStudentRate(double topStudentRate) {
        this.topStudentRate = topStudentRate;
    }

    public ProjectConfig() {
    }

    public ProjectConfig(String projectId) {
        this.projectId = projectId;
    }

    public List<String> getRankLevelCombines() {
        return rankLevelCombines;
    }

    public void setRankLevelCombines(List<String> rankLevelCombines) {
        this.rankLevelCombines = rankLevelCombines;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public int getRankSegmentCount() {
        return rankSegmentCount;
    }

    public void setRankSegmentCount(int rankSegmentCount) {
        this.rankSegmentCount = rankSegmentCount;
    }

    public Map<String, Double> getScoreLevels() {
        return scoreLevels;
    }

    public void setScoreLevels(Map<String, Double> scoreLevels) {
        this.scoreLevels = scoreLevels;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public boolean isCombineCategorySubjects() {
        return combineCategorySubjects;
    }

    public void setCombineCategorySubjects(boolean combineCategorySubjects) {
        this.combineCategorySubjects = combineCategorySubjects;
    }

    public Map<String, Double> getRankLevels() {
        return rankLevels;
    }

    public void setRankLevels(Map<String, Double> rankLevels) {
        this.rankLevels = rankLevels;
    }

    public void addRankingLevel(String level, double portion) {
        if (this.rankLevels == null) {
            this.rankLevels = new HashMap<>();
        }
        this.rankLevels.put(level, portion);
    }

    public String getLastRankLevel() {

        if (rankLevels.isEmpty()) {
            return "";
        }

        List<String> levels = new ArrayList<>(rankLevels.keySet());
        Collections.sort(levels);
        Collections.reverse(levels);
        return levels.get(0);
    }

    public void addScoreLevel(String scoreLevel, double rate) {
        if (this.scoreLevels == null) {
            this.scoreLevels = new HashMap<>();
        }
        this.scoreLevels.put(scoreLevel, rate);
    }
}
