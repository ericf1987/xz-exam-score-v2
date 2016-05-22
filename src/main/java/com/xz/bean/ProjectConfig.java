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
        List<String> levels = new ArrayList<>(rankLevels.keySet());
        Collections.sort(levels);
        Collections.reverse(levels);
        return levels.get(0);
    }
}
