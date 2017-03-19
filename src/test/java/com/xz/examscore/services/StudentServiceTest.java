package com.xz.examscore.services;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.server.sys.QueryExamSubjects;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import org.bson.Document;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
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
        Range range = Range.school("15e70531-5ac0-475d-a2da-2fc04242ac75");
        int studentCount = studentService.getStudentCount("430300-32d8433951ce43cab5883abff77c8ea3", range);
        System.out.println(studentCount);
    }

    @Test
    public void testGetStudentCount1() throws Exception {
        Range range = new Range("school", "0e7ea9fd-ce47-4f6a-a1fd-129603198b79");
        int studentCount = studentService.getStudentCount("430200-83943be3c36f43a8aab0b545e66dbe3d", "007", range);
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

    @Test
    public void testPickStudentsByRange() throws Exception {
        String projectId = "430300-672a0ed23d9148e5a2a31c8bf1e08e62";
        List<String> studentIds = Arrays.asList("7beb04a1-a9a0-4255-85b8-f0c98da9f7db",
                "6f5d2ede-1acb-463a-acb4-4230a5e139c9",
                "37ec8040-994e-4223-bf14-fb5e2892c498",
                "5ef5d89c-684c-49a8-94a4-228dcc2546d3",
                "ff1b6153-99b5-475f-985c-265cc423067b",
                "6cd5d31e-193a-4aae-9c03-bb92c493f816",
                "8c7933e8-6777-436f-9e7c-b4a788c75301",
                "7ab1a822-45a9-40e7-b753-95d16a95534e");
        ArrayList<Document> school = studentService.pickStudentsByRange(projectId, studentIds, "school");
        System.out.println(school);
    }
}