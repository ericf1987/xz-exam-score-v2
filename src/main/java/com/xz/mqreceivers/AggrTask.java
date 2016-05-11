package com.xz.mqreceivers;

import com.xz.bean.Range;
import com.xz.bean.Target;

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

    private Target target;

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

    public AggrTask(String projectId, String type, Target target) {
        this.projectId = projectId;
        this.type = type;
        this.target = target;
    }

    public Target getTarget() {
        return target;
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

    public AggrTask setTarget(Target target) {
        this.target = target;
        return this;
    }

    public AggrTask setTarget(String name, Object id) {
        return setTarget(new Target(name, id));
    }

    @Override
    public String toString() {
        return "AggrTask{" +
                "projectId='" + projectId + '\'' +
                ", type='" + type + '\'' +
                ", range=" + range +
                ", target=" + target +
                '}';
    }
}
