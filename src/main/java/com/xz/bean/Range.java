package com.xz.bean;

/**
 * (description)
 * created at 16/05/10
 *
 * @author yiding_he
 */
public class Range {

    public static final String PROVINCE = "province";

    public static final String CITY = "city";

    public static final String AREA = "area";

    public static final String SCHOOL = "school";

    public static final String CLASS = "class";

    private String name;

    private String id;

    public Range() {
    }

    public Range(String name, String id) {
        this.name = name;
        this.id = id;
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
        return "Range{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
