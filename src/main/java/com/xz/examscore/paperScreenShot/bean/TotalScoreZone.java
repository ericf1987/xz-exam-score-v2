package com.xz.examscore.paperScreenShot.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * @author by fengye on 2017/3/15.
 */
public class TotalScoreZone {

    private double coordinateX;

    private double coordinateY;

    private double totalScore;

    private Map<String, Integer> rankMap = new HashMap<>();

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

    public double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(double totalScore) {
        this.totalScore = totalScore;
    }

    public Map<String, Integer> getRankMap() {
        return rankMap;
    }

    public void setRankMap(Map<String, Integer> rankMap) {
        this.rankMap = rankMap;
    }

    public TotalScoreZone(double coordinateX, double coordinateY, double totalScore, Map<String, Integer> rankMap) {
        this.coordinateX = coordinateX;
        this.coordinateY = coordinateY;
        this.totalScore = totalScore;
        this.rankMap = rankMap;
    }

    public String getRankDesc(TotalScoreZone zone){
        StringBuilder builder = new StringBuilder();
        if(null != zone){
            Map<String, Integer> rankMap = zone.getRankMap();
            rankMap.forEach((k, v) -> builder.append(k).append("排名：").append(v).append(",").append("\n"));
            return builder.toString().substring(0, builder.length() - 1);
        }else{
            return "";
        }
    }
}
