package com.xz.bean;

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
     * 得分等级配置
     */
    private Map<String, Double> scoreLevels = new HashMap<>();

    public List<String> getDisplayOptions() {
        return displayOptions;
    }

    public void setDisplayOptions(List<String> displayOptions) {
        this.displayOptions = displayOptions;
    }

    /**
     * 考试等第配置信息
     */
    private List<String> displayOptions = new ArrayList<>();

    private String startDate;

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
