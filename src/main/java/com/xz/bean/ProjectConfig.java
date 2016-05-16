package com.xz.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * 报表的项目个性化配置
 *
 * @author yiding_he
 */
public class ProjectConfig {

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
    private Map<String, Double> rankingLevels = new HashMap<>();

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

    public Map<String, Double> getRankingLevels() {
        return rankingLevels;
    }

    public void setRankingLevels(Map<String, Double> rankingLevels) {
        this.rankingLevels = rankingLevels;
    }

    public void addRankingLevel(String level, double portion) {
        if (this.rankingLevels == null) {
            this.rankingLevels = new HashMap<>();
        }
        this.rankingLevels.put(level, portion);
    }
}
