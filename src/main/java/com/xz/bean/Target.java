package com.xz.bean;

import com.alibaba.fastjson.JSON;

import java.util.Objects;

/**
 * (description)
 * created at 16/05/10
 *
 * @author yiding_he
 */
@SuppressWarnings("unchecked")
public class Target {

    public static final String PROJECT = "project";

    public static final String SUBJECT = "subject";

    public static final String SUBJECT_OBJECTIVE = "subjectObjective";

    public static final String QUEST = "quest";

    public static final String QUEST_TYPE = "questType";

    public static final String POINT = "point";

    public static final String ABILITY_LEVEL = "abilityLevel";

    private String name;

    private Object id;

    public Target() {
    }

    public Target(String name, Object id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getId() {
        return id;
    }

    public <T> T getId(Class<T> type) {
        if (type.isAssignableFrom(this.id.getClass())) {
            return (T) this.id;
        } else {
            return JSON.toJavaObject((JSON) JSON.toJSON(this.id), type);
        }
    }

    public Target setId(Object id) {
        this.id = id;
        return this;
    }

    @Override
    public String toString() {
        return "Target{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                '}';
    }

    public boolean match(String target) {
        return Objects.equals(target, this.name);
    }
}
