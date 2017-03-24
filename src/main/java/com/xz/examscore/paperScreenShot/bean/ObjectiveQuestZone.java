package com.xz.examscore.paperScreenShot.bean;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author by fengye on 2017/3/15.
 */
public class ObjectiveQuestZone {

    private double totalScore;

    private int correctCount;

    private int totalCount;

    private double coordinateX;

    private double coordinateY;

    private Map<String, Integer> rankMap = new HashMap<>();

    private List<String> errorQuests = new LinkedList<>();

    public double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(double totalScore) {
        this.totalScore = totalScore;
    }

    public List<String> getErrorQuests() {
        return errorQuests;
    }

    public void setErrorQuests(List<String> errorQuests) {
        this.errorQuests = errorQuests;
    }

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

    public String getCorrectDecs(ObjectiveQuestZone zone) {
//        return zone == null ? "" : zone.getCorrectCount() + "/" + zone.getTotalCount() + " 错题：";
        return zone == null ? "" : zone.getTotalScore() + "分 错题：";
    }

    /**
     * 将错误描述截取成多个文字区域
     *
     * @param errorDesc   错误描述信息
     * @param errorDescX  错误描述信息的起始位置X
     * @param coordinateY 错误描述信息的起始位置Y
     * @param font        字体
     * @return 文本区域对象列表
     */
    public List<TextRect> getTextRects(List<String> errorDesc, double errorDescX, double coordinateY, Font font) {

        int fontSize = font.getSize();

        //剩余宽度中可以容纳的字符数
        int count = 10;

        int size = errorDesc.size();

        //行数
        int row = errorDesc.size() / count;

        List<TextRect> textRects = new LinkedList<>();
        for (int x = 0; x <= row; x++) {
            int tail = count * (x + 1);
            int lastIndex = tail > size ? size : tail;
            TextRect textRect = new TextRect((float) errorDescX, (float) coordinateY + fontSize * x,
                    getErrorNoString(errorDesc.subList(count * x, lastIndex)), font);
            textRects.add(textRect);
        }

        return textRects;
    }

    public String getErrorNoString(List<String> errorQuests) {
        StringBuilder builder = new StringBuilder();
        if (null != errorQuests && !errorQuests.isEmpty()) {
            for (String errorNo : errorQuests) {
                builder.append(errorNo).append("、");
            }
            return builder.substring(0, builder.length() - 1);
        }
        return "";
    }
}
