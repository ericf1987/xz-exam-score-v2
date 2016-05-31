package com.xz.report;

import com.xz.ajiaedu.common.lang.Context;

public class SheetTask extends Context {

    private String title;

    private Class<? extends SheetGenerator> generatorClass;

    public SheetTask() {
    }

    public SheetTask(String title, Class<? extends SheetGenerator> generatorClass) {
        this.title = title;
        this.generatorClass = generatorClass;
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
