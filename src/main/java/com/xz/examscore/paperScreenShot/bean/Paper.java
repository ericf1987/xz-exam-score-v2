package com.xz.examscore.paperScreenShot.bean;

import java.util.LinkedList;
import java.util.List;

/**
 * @author by fengye on 2017/3/15.
 */
public class Paper {
    private TotalScoreZone totalScoreZone;

    private ObjectiveQuestZone objectiveQuestZone;

    private List<Rect> rects = new LinkedList<>();

    public TotalScoreZone getTotalScoreZone() {
        return totalScoreZone;
    }

    public void setTotalScoreZone(TotalScoreZone totalScoreZone) {
        this.totalScoreZone = totalScoreZone;
    }

    public ObjectiveQuestZone getObjectiveQuestZone() {
        return objectiveQuestZone;
    }

    public void setObjectiveQuestZone(ObjectiveQuestZone objectiveQuestZone) {
        this.objectiveQuestZone = objectiveQuestZone;
    }

    public List<Rect> getRects() {
        return rects;
    }

    public void setRects(List<Rect> rects) {
        this.rects = rects;
    }
}
