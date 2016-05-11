package com.xz.bean;

/**
 * (description)
 * created at 16/05/10
 *
 * @author yiding_he
 */
public class Target {

    public static final String PROJECT = "project";

    public static final String SUBJECT = "subject";

    public static final String QUEST = "quest";

    public static final String QUEST_TYPE = "questType";

    public static final String POINT = "point";

    public static final String ABILITY_LEVEL = "abilityLevel";

    private String name;

    private String id;

    private String subjectId;   // 当 name 为 quest 时，id 仅包含 questNo，科目 ID 信息保存在这里

    public Target() {
    }

    public Target(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public Target setSubjectId(String subjectId) {
        this.subjectId = subjectId;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Target{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", subjectId='" + subjectId + '\'' +
                '}';
    }
}
