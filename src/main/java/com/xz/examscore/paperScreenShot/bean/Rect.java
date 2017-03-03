package com.xz.examscore.paperScreenShot.bean;

/**
 * @author by fengye on 2017/3/1.
 */
public class Rect {
    private double coordinateX;
    private double coordinateY;
    private double height;
    private double width;
    private int pageIndex;
    private double fullScore;
    private double score;

    private String paper_positive;
    private String paper_reverse;

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

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public String getPaper_positive() {
        return paper_positive;
    }

    public void setPaper_positive(String paper_positive) {
        this.paper_positive = paper_positive;
    }

    public String getPaper_reverse() {
        return paper_reverse;
    }

    public void setPaper_reverse(String paper_reverse) {
        this.paper_reverse = paper_reverse;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public double getFullScore() {
        return fullScore;
    }

    public void setFullScore(double fullScore) {
        this.fullScore = fullScore;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public Rect() {
    }

    public Rect(double coordinateX, double coordinateY, double height, double width, int pageIndex, String paper_positive, String paper_reverse) {
        this.coordinateX = coordinateX;
        this.coordinateY = coordinateY;
        this.height = height;
        this.width = width;
        this.pageIndex = pageIndex;
        this.paper_positive = paper_positive;
        this.paper_reverse = paper_reverse;
    }

    @Override
    public String toString() {
        return "Rect{" +
                "coordinateX=" + coordinateX +
                ", coordinateY=" + coordinateY +
                ", height=" + height +
                ", width=" + width +
                ", pageIndex=" + pageIndex +
                ", fullScore=" + fullScore +
                ", score=" + score +
                ", paper_positive='" + paper_positive + '\'' +
                ", paper_reverse='" + paper_reverse + '\'' +
                '}';
    }
}
