package com.xz.services;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.bean.Range;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * (description)
 * created at 16/05/10
 *
 * @author yiding_he
 */
public class StudentCountServiceTest extends XzExamScoreV2ApplicationTests {

    public static final String PROJECT = "FAKE_PROJECT_1";

    @Autowired
    StudentCountService studentCountService;

    @Test
    public void testGetStudentCount() throws Exception {
        Range range = new Range("school", "SCHOOL_001");
        int studentCount = studentCountService.getStudentCount(PROJECT, range);
        System.out.println(studentCount);
    }

    @Test
    public void testGetStudentCount1() throws Exception {
        Range range = new Range("school", "SCHOOL_001");
        int studentCount = studentCountService.getStudentCount(PROJECT, "001", range);
        System.out.println(studentCount);
    }
}