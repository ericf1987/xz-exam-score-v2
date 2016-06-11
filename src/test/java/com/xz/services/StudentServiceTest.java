package com.xz.services;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.bean.Range;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * (description)
 * created at 16/05/10
 *
 * @author yiding_he
 */
public class StudentServiceTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    StudentService studentService;

    @Test
    public void testGetStudentCount() throws Exception {
        Range range = Range.province("430000");
        int studentCount = studentService.getStudentCount(PROJECT_ID, range);
        System.out.println(studentCount);
    }

    @Test
    public void testGetStudentCount1() throws Exception {
        Range range = new Range("school", "11b66fc2-8a76-41c2-a1b3-5011523c7e47");
        int studentCount = studentService.getStudentCount(PROJECT_ID, "001", range);
        System.out.println(studentCount);
    }

    @Test
    public void testGetStudentList() throws Exception {
        Range range = new Range("class", "SCHOOL_001_CLASS_01");
        List<String> studentList = studentService.getStudentIds(PROJECT_ID, "004005006", range);
        studentList.forEach(System.out::println);
    }
}