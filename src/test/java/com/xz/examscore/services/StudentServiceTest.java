package com.xz.examscore.services;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.ajiaedu.common.ajia.Param;
import com.xz.examscore.api.server.sys.QueryExamSubjects;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import org.bson.Document;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;
import static com.xz.ajiaedu.common.mongo.MongoUtils.toList;

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

    @Autowired
    SubjectService subjectService;

    @Test
    public void testGetStudentCount() throws Exception {
        Range range = Range.school("15e70531-5ac0-475d-a2da-2fc04242ac75");
        int studentCount = studentService.getStudentCount("430300-32d8433951ce43cab5883abff77c8ea3", range);
        System.out.println(studentCount);
    }

    @Test
    public void testGetStudentCount1() throws Exception {
        Range range = new Range("class", "b31a7c9d-ff3e-49e1-93ef-c5c9a5afa14d");
        int studentCount = studentService.getStudentCount("430100-f779e2171766442a80afd512add13856", null, range);
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
        String projectId = "430300-c582131e66b64fe38da7d0510c399ec4";
        String province = "430000";
        List<String> studentList = studentService.getStudentIds(projectId, Range.province(province), Target.project(projectId));
        int count = studentService.getStudentCount(projectId, Range.province(province));
        System.out.println(studentList.toString());
        System.out.println(count);
    }

    @Test
    public void test2() throws Exception {
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

    @Test
    public void testGetProjectStudentList() throws Exception {
        String projectId = "430600-12b3be890aa840c58cccdfd48b1c8a8f";
        Range range = Range.clazz("4fd9984d-23fb-43ce-9aa3-c47cb1c2e229");
        Document projection = doc("student", 1).append("name", 1).append("school", 1).append("class", 1);
        Document sort = doc("school", 1).append("class", 1);
        List<Document> documents = toList(studentService.getProjectStudentList(projectId, range, 0, 0, projection, sort));
        System.out.println(documents.toString());
        System.out.println(documents.size());
    }

    @Test
    public void testhasAllSubjectsStudent() throws Exception {
        String projectId = "430000-79eee8ac6c244d92a24dbcc66a2ffda2";
        Document projection = doc("student", 1).append("name", 1).append("school", 1).append("class", 1);
        Document sort = doc("school", 1).append("class", 1);
        List<String> subjects = subjectService.querySubjects(projectId);
        ArrayList<Document> documents = studentService.hasAllSubjectsStudent(projectId, subjects, null, 0, 0, projection, sort);
        System.out.println(documents.size());
        List<Document> students = documents.stream().filter(s -> studentService.isRequiredStudent(projectId, s.getString("student"), subjects))
                .collect(Collectors.toList());
        System.out.println(students.size());
    }
}