package com.xz.mqreceivers;

import com.xz.bean.Range;

/**
 * (description)
 * created at 16/05/10
 *
 * @author yiding_he
 */
public class AggrTask {

    private String projectId;

    private String type;

    private Range range;

    public AggrTask() {
    }

    public AggrTask(String projectId, String type) {
        this.projectId = projectId;
        this.type = type;
    }

    public AggrTask(String projectId, String type, Range range) {
        this.projectId = projectId;
        this.type = type;
        this.range = range;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getProjectId() {
        return projectId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Range getRange() {
        return range;
    }

    public AggrTask setRange(Range range) {
        this.range = range;
        return this;
    }

    public AggrTask setRange(String name, String id) {
        return setRange(new Range(name, id));
    }
}
