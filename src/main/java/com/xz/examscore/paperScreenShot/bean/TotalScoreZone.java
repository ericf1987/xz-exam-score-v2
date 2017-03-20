package com.xz.examscore.paperScreenShot.bean;

import java.util.List;

/**
 * @author by fengye on 2017/3/15.
 */
public class TotalScoreZone {

    private double coordinateX;

    private double coordinateY;

    private double totalScore;

    private List<TextRect> textRects;

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

    public List<TextRect> getTextRects() {
        return textRects;
    }

    public void setTextRects(List<TextRect> textRects) {
        this.textRects = textRects;
    }

    public TotalScoreZone(double coordinateX, double coordinateY, double totalScore, List<TextRect> textRects) {
        this.coordinateX = coordinateX;
        this.coordinateY = coordinateY;
        this.totalScore = totalScore;
        this.textRects = textRects;
    }


}
