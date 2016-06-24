package com.xz;

import com.xz.ajiaedu.common.beans.user.Student;
import com.xz.ajiaedu.common.beans.user.Teacher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * (description)
 * created at 16/06/06
 *
 * @author yiding_he
 */
public class Test {

    public static void main(String[] args) {

/*        List<Teacher> teachers = new ArrayList<>();
        List<Student> students = new ArrayList<>();
        List<Teacher> nonStudentTeachers = new ArrayList<>(teachers);

        Predicate<Teacher> isMarked = t -> {
            Predicate<Student> contains = _t -> t.getId().equals(_t.getId());
            return students.stream().anyMatch(contains);
        };

        nonStudentTeachers.removeIf(isMarked);

        Map<String, Teacher> teacherMap = new HashMap<>();*/
        System.out.println(Math.floor(13.75));
        System.out.println((int)Math.ceil(13.75));
        System.out.println(Double.valueOf(13.75).intValue());
        System.out.println((double)11 / 2.1);
    }
}
