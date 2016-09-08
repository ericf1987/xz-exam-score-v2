package com.xz.examscore.services;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.server.sys.QueryExamSubjects;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
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

    public static final String PROJECT = "430100-a05db0d05ad14010a5c782cd31c0283f";

    @Autowired
    StudentService studentService;

    @Autowired
    RangeService rangeService;

    @Autowired
    QueryExamSubjects queryExamSubjects;

    @Test
    public void testGetStudentCount() throws Exception {
        Range range = Range.province("430000");
        int studentCount = studentService.getStudentCount(PROJECT, range);
        System.out.println(studentCount);
    }

    @Test
    public void testGetStudentCount1() throws Exception {
        Range range = new Range("school", "11b66fc2-8a76-41c2-a1b3-5011523c7e47");
        int studentCount = studentService.getStudentCount(XT_PROJECT_ID, "001", range);
        System.out.println(studentCount);
    }

    @Test
    public void testGetClassSubjectStudentCount() throws Exception {
        Range range = Range.clazz("a1895cd9-d82c-4b12-a698-164fb5ceb1f3");
        int studentCount = studentService.getStudentCount(PROJECT, "003", range);
        System.out.println(studentCount);
    }

    @Test
    public void testGetStudentList() throws Exception {
        Range range = new Range("class", "a1895cd9-d82c-4b12-a698-164fb5ceb1f3");
        List<String> studentList = studentService.getStudentIds(PROJECT, "003", range);
        studentList.forEach(System.out::println);
    }

    @Test
    public void testGetStudentCount2() throws Exception {
        String project = "430100-8d805ef37b2f4bc7ad9808a9a109dc22";
        String school = "091fbca6-ab16-49e5-9ac5-bae0538ece14";
        int studentCount = studentService.getStudentCount(project, Range.school(school));
        System.out.println(studentCount);
    }

    @Test
    public void testGetStudentIds() throws Exception {
        String projectId = "430100-e7bd093d92d844819c7eda8b641ab6ee";
        String schoolId = "d00faaa0-8a9b-45c4-ae16-ea2688353cd0";
        String subjectId = "001";
        List<String> studentList = studentService.getStudentIds(projectId, Range.school(schoolId), Target.subject(subjectId));
        int count = studentService.getStudentCount(projectId, Range.school(schoolId));
        System.out.println(studentList.toString());
        System.out.println(count);
    }

    @Test
    public void test2() throws Exception{
        String projectId = "430300-672a0ed23d9148e5a2a31c8bf1e08e62";
        String schoolId = "11b66fc2-8a76-41c2-a1b3-5011523c7e47";
        Param param = new Param().setParameter("projectId", projectId).setParameter("schoolId", schoolId);
        Result result = queryExamSubjects.execute(param);
        System.out.println(result.getData());

    }
}