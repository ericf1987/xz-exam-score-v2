package com.xz.mqreceivers;

import com.xz.bean.Range;

/**
 * (description)
 * created at 16/05/10
 *
 * @author yiding_he
 */
public class AggrTask {

    private String type;

    private Range range;

    public AggrTask() {
    }

    public AggrTask(String type) {
        this.type = type;
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
