package com.xz.bean;

import java.util.List;

/**
 * 知识点
 * created at 16/06/12
 *
 * @author yiding_he
 */
public class Point {

    private String id;

    private String name;

    private List<String> levels;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getLevels() {
        return levels;
    }

    public void setLevels(List<String> levels) {
        this.levels = levels;
    }
}
