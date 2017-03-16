package com.xz.examscore.paperScreenShot.bean;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author by fengye on 2017/3/15.
 */
public class ObjectiveQuestZone {
    private int correctCount;

    private int totalCount;

    private double coordinateX;

    private double coordinateY;

    private Map<String, Integer> rankMap = new HashMap<>();

    private List<String> errorQuests = new LinkedList<>();


    public int getCorrectCount() {
        return correctCount;
    }

    public void setCorrectCount(int correctCount) {
        this.correctCount = correctCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public double getCoordinateX() {
        return coordinateX;
    }

    public void setCoordinateX(double coordinateX) {
        this.coordinateX = coordinateX;
    }

    public double getCoordinateY() {
        return coordinateY;
    }

    public void setCoordinateY(double coordinateY) {
        this.coordinateY = coordinateY;
    }

    public Map<String, Integer> getRankMap() {
        return rankMap;
    }

    public void setRankMap(Map<String, Integer> rankMap) {
        this.rankMap = rankMap;
    }

    public List<String> getErrorQuestList() {
        return errorQuests;
    }

    public void setErrorQuestList(List<String> errorQuests) {
        this.errorQuests = errorQuests;
    }

    public String getErrorDesc(ObjectiveQuestZone zone){
        return zone == null ? "" : "错题：" + zone.getErrorQuestList().toString();
    }

    public String getCorrectDecs(ObjectiveQuestZone zone){
        return zone == null ? "" : zone.getCorrectCount() + "/" + zone.getTotalCount();
    }
}
