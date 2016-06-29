package com.xz.bean;

/**
 * (description)
 * created at 16/06/29
 *
 * @author yiding_he
 */
public class SubjectLevel {

    private String subject;

    private String level;

    public SubjectLevel() {
    }

    public SubjectLevel(String subject, String level) {
        this.subject = subject;
        this.level = level;
    }

    public String getSubject() {
        return subject;
    }

    public String getLevel() {
        return level;
    }
}
