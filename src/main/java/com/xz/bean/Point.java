package com.xz.bean;

import java.io.Serializable;

/**
 * 知识点
 * created at 16/06/12
 *
 * @author yiding_he
 */
public class Point implements Serializable {

    private String id;

    private String name;

    public Point() {
    }

    public Point(String id, String name) {
        this.id = id;
        this.name = name;
    }

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

    @Override
    public String toString() {
        return "Point{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
