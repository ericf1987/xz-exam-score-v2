package com.xz.examscore.api.annotation;

/**
 * 基本类型
 *
 * @author zhaorenwu
 */
public enum Type {

    Boolean, BooleanArray("Boolean[]"),
    Integer, IntegerArray("Integer[]"),
    Decimal, DecimalArray("Decimal[]"),
    String, StringArray("String[]"),
    Date, DateArray,
    List, Pojo;

    private String stringValue = null;

    private Type() {
    }

    private Type(String stringValue) {
        this.stringValue = stringValue;
    }

    @Override
    public String toString() {
        return this.stringValue == null ? super.toString() : this.stringValue;
    }
}
