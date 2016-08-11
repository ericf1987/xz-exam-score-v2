package com.xz.intclient;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.ajiaedu.common.aliyun.ApiResponse;
import com.xz.api.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * (description)
 * created at 16/06/01
 *
 * @author yiding_he
 */
public class InterfaceClientTest extends XzExamScoreV2ApplicationTests {

    public static final String PROJECT_ID = "430100-e7bd093d92d844819c7eda8b641ab6ee";

    public static final String SCHOOL_ID = "d00faaa0-8a9b-45c4-ae16-ea2688353cd0";

    public static final String CLASS_ID = "048eb56f-a105-4992-8228-0e436c9e4670";

    @Autowired
    InterfaceClient interfaceClient;

    @Test
    public void testQueryKnowledgePointById() throws Exception {
        JSONObject jsonObject = interfaceClient.queryKnowledgePointById("1022505");
        System.out.println(jsonObject);
    }

    @Test
    public void testQueryQuestionByProject() throws Exception {
        JSONArray quests = interfaceClient.queryQuestionByProject(PROJECT_ID);
        assertNotNull(quests);
        assertFalse(quests.isEmpty());
    }

    @Test
    public void testQueryExamSchoolByProject() throws Exception {
        JSONArray schools = interfaceClient.queryExamSchoolByProject(PROJECT_ID, false);
        assertNotNull(schools);
        assertFalse(schools.isEmpty());
    }

    @Test
    public void testQueryExamClassByProject() throws Exception {
        JSONArray classes = interfaceClient.queryExamClassByProject(PROJECT_ID, SCHOOL_ID, false);
        assertNotNull(classes);
        assertFalse(classes.isEmpty());
    }

    @Test
    public void testQueryClassExamStudent() throws Exception {
        JSONArray students = interfaceClient.queryClassExamStudent(PROJECT_ID, CLASS_ID);
        assertNotNull(students);
        assertFalse(students.isEmpty());
    }

    @Test
    public void testQuerySubjectListByProjectId() throws Exception {
        JSONArray subjects = interfaceClient.querySubjectListByProjectId(PROJECT_ID);
        assertNotNull(subjects);
        assertFalse(subjects.isEmpty());
    }

    @Test
    public void testQueryProjectById() throws Exception {
        JSONObject project = interfaceClient.queryProjectById("430100-405f6648c79545d589687a1d0763fc9f");
        System.out.println(project.toString());
        assertNotNull(project);
        assertEquals(PROJECT_ID, project.getString("id"));
        assertEquals(8, project.getIntValue("id"));
    }

    @Test
    public void testQueryProjectReportConfig() throws Exception {
        ApiResponse result = interfaceClient.queryProjectReportConfig("430100-e7bd093d92d844819c7eda8b641ab6ee");
//        JSONObject rankLevel = result.get("rankLevel");
//        System.out.println(rankLevel.toString());
        System.out.println(result.getData());
    }

    @Test
    public void testAddRpApplyOpen() throws Exception {
        Param param = new Param().setParameter("pageSize", 10).setParameter("pageIndex", 0);
        ApiResponse result = interfaceClient.listRpApplyOpen(param);
        System.out.println(result.getData());
    }

}