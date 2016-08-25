package com.xz.examscore.asynccomponents.aggrtask;

import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;

/**
 * (description)
 * created at 16/05/10
 *
 * @author yiding_he
 */
public class AggrTaskInfo {

    private String projectId;

    private String aggregationId;

    private String type;

    private Range range;

    private Target target;

    public AggrTaskInfo() {
    }

    public AggrTaskInfo(String projectId, String aggregationId, String type) {
        this.projectId = projectId;
        this.aggregationId = aggregationId;
        this.type = type;
    }

    public AggrTaskInfo(String projectId, String aggregationId, String type, Range range) {
        this.projectId = projectId;
        this.aggregationId = aggregationId;
        this.type = type;
        this.range = range;
    }

    public AggrTaskInfo(String projectId, String aggregationId, String type, Target target) {
        this.projectId = projectId;
        this.aggregationId = aggregationId;
        this.type = type;
        this.target = target;
    }

    public String getAggregationId() {
        return aggregationId;
    }

    public void setAggregationId(String aggregationId) {
        this.aggregationId = aggregationId;
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

    public AggrTaskInfo setRange(Range range) {
        this.range = range;
        return this;
    }

    public AggrTaskInfo setRange(String name, String id) {
        return setRange(new Range(name, id));
    }

    public AggrTaskInfo setTarget(Target target) {
        this.target = target;
        return this;
    }

    public AggrTaskInfo setTarget(String name, Object id) {
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
