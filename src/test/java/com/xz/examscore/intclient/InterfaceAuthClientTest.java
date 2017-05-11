package com.xz.examscore.intclient;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * @author by fengye on 2017/5/8.
 */
public class InterfaceAuthClientTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    InterfaceAuthClient interfaceAuthClient;

    public static final String PROJECT_ID = "430100-b05a111c72c740f4898660a057c48e28";

    @Test
    public void testQueryKnowledgePointById() throws Exception {
        JSONObject jsonObject = interfaceAuthClient.queryKnowledgePointById("1007348");
        System.out.println(jsonObject);
    }

    @Test
    public void testQueryQuestionByProject() throws Exception {
        JSONArray quests = interfaceAuthClient.queryQuestionByProject(PROJECT_ID);
        Map<String, Object> map = interfaceAuthClient.queryQuestionByProject(PROJECT_ID, true);
        System.out.println(quests);
        System.out.println(map);
    }

    @Test
    public void testQueryExamSchoolByProject() throws Exception {
        JSONArray jsonArray = interfaceAuthClient.queryExamSchoolByProject(PROJECT_ID, false);
        System.out.println(jsonArray);
    }

    @Test
    public void testQueryExamClassByProject() throws Exception {
        JSONArray jsonArray = interfaceAuthClient.queryExamClassByProject(
                "430100-501b96776dc348748e2afdb95d491516",
                "ad364e60-20ce-4063-a67e-beeb438d57e9",
                false
        );
        System.out.println(jsonArray);
    }

    @Test
    public void testQueryClassExamStudent() throws Exception {
        JSONArray jsonArray = interfaceAuthClient.queryClassExamStudent(
                "431200-5c78e22cb1e64e4caa9583d35ad92658",
                "eebdc578-fa1e-4dfc-b50f-7d17be9fb540"
        );
        System.out.println(jsonArray);
    }

    @Test
    public void testQuerySubjectListByProjectId() throws Exception {
        JSONArray jsonArray = interfaceAuthClient.querySubjectListByProjectId(PROJECT_ID);
        System.out.println(jsonArray);
    }

    @Test
    public void testQueryProjectById() throws Exception {
        JSONObject jsonObject = interfaceAuthClient.queryProjectById("430100-354dce3ac8ef4800a1b57f81a10b8baa");
        System.out.println(jsonObject);
    }

    @Test
    public void testImportExamScoreFromOSS() throws Exception {

    }

    @Test
    public void testAddRpFeedbackInfo() throws Exception {

    }

    @Test
    public void testReleaseExamScore() throws Exception {

    }

    @Test
    public void testAddRpApplyOpen() throws Exception {

    }

    @Test
    public void testQueryProjectReportConfig() throws Exception {
        Result result = interfaceAuthClient.queryProjectReportConfig("430100-354dce3ac8ef4800a1b57f81a10b8baa");
        System.out.println(result.getData());
    }

    @Test
    public void testSetProjectConfig() throws Exception {

    }

    @Test
    public void testQueryAllSubjects() throws Exception {
        JSONArray jsonArray = interfaceAuthClient.queryAllSubjects();
        System.out.println(jsonArray);
    }
}