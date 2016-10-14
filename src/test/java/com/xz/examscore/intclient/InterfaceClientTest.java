package com.xz.examscore.intclient;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xz.ajiaedu.common.aliyun.ApiResponse;
import com.xz.ajiaedu.common.lang.CounterMap;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.api.Param;
import com.xz.examscore.services.ImportProjectService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * (description)
 * created at 16/06/01
 *
 * @author yiding_he
 */
public class InterfaceClientTest extends XzExamScoreV2ApplicationTests {

    public static final String PROJECT_ID = "430100-eddd6548432c4e5fab54739101c0affb";

    public static final String SCHOOL_ID = "d00faaa0-8a9b-45c4-ae16-ea2688353cd0";

    public static final String CLASS_ID = "048eb56f-a105-4992-8228-0e436c9e4670";

    @Autowired
    InterfaceClient interfaceClient;

    @Autowired
    ImportProjectService importProjectService;

    @Test
    public void testQueryKnowledgePointById() throws Exception {
        JSONObject jsonObject = interfaceClient.queryKnowledgePointById("1007348");
        System.out.println(jsonObject);
    }

    @Test
    public void testQueryQuestionByProject() throws Exception {
        JSONArray quests = interfaceClient.queryQuestionByProject("430100-2c641a3e36ff492aa535da7fb4cf28cf");
        System.out.println(quests.toString());
        Map<String, Double> map = new HashMap<>();
        map.put("score", 0d);
        quests.forEach(quest -> {
            JSONObject obj = (JSONObject)quest;
            if(obj.getString("subjectId").equals("001"))
                map.put("score", map.get("score") + Double.parseDouble(obj.getString("score")));
        });
        System.out.println(map.get("score"));
        assertNotNull(quests);
        assertFalse(quests.isEmpty());
    }

    @Test
    public void testQuestQuestionByProject2() throws Exception {
        JSONArray quests = interfaceClient.queryQuestionByProject("433100-0372aa59ae4841618138c65e9ee18314");
        System.out.println(quests.toString());
        Map<String, Double> mm = importProjectService.gatherQuestScoreBySubject(quests);
        System.out.println(importProjectService.separateSubject("004005006").toString());
        System.out.println(mm);
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
        System.out.println(subjects.toString());
        assertNotNull(subjects);
        assertFalse(subjects.isEmpty());
    }

    @Test
    public void testQueryProjectById() throws Exception {
        JSONObject project = interfaceClient.queryProjectById("430100-553137a1e78741149104526aaa84393e");
        System.out.println(project.toString());
        assertNotNull(project);
        assertEquals(PROJECT_ID, project.getString("id"));
        assertEquals(8, project.getIntValue("id"));
    }

    @Test
    public void testQueryProjectReportConfig() throws Exception {
        ApiResponse result = interfaceClient.queryProjectReportConfig("430100-2c641a3e36ff492aa535da7fb4cf28cf");
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