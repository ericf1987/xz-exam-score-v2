package com.xz.report;

import com.xz.ajiaedu.common.lang.Context;
import com.xz.bean.Range;
import com.xz.bean.Target;

public class SheetTask extends Context {

    private String title;

    private Class<? extends SheetGenerator> generatorClass;

    private Range range;

    private Target target;

    public SheetTask() {
    }

    public SheetTask(String title, Class<? extends SheetGenerator> generatorClass) {
        this.title = title;
        this.generatorClass = generatorClass;
    }

    public Range getRange() {
        return range;
    }

    public void setRange(Range range) {
        this.range = range;
    }

    public Target getTarget() {
        return target;
    }

    public void setTarget(Target target) {
        this.target = target;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Class<? extends SheetGenerator> getGeneratorClass() {
        return generatorClass;
    }

    public void setGeneratorClass(Class<? extends SheetGenerator> generatorClass) {
        this.generatorClass = generatorClass;
    }
}
