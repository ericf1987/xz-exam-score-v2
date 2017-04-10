package com.xz.examscore.intclient;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xz.ajiaedu.common.aliyun.ApiResponse;
import com.xz.ajiaedu.common.json.JSONUtils;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.api.Param;
import com.xz.examscore.services.ImportProjectService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

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
        JSONArray quests = interfaceClient.queryQuestionByProject("431100-ac367ba398d744d489e9de4ed225b755");

        System.out.println(quests.toJSONString());

        //指定科目和指定标答
        Predicate<JSONObject> predicate = (p) -> p.getString("subjectId").equals("002") && p.getString("answer").equals("D");

        JSONObject jsonObject = JSONUtils.getFromJSONArray(quests, predicate);

        System.out.println(jsonObject.toJSONString());

/*        quests.stream().filter(q -> {
            JSONObject obj = (JSONObject)q;
            if(obj.getString("subjectId").equals("001")){
                System.out.println(obj.toString());
                return true;
            }
            return false;
        });*/

/*        System.out.println(quests.toString());
        Map<String, Double> map = new HashMap<>();
        map.put("score", 0d);
        quests.forEach(quest -> {
            JSONObject obj = (JSONObject)quest;
            if(obj.getString("subjectId").equals("001"))
                map.put("score", map.get("score") + Double.parseDouble(obj.getString("score")));
        });
        System.out.println(map.get("score"));
        assertNotNull(quests);
        assertFalse(quests.isEmpty());*/
    }

    @Test
    public void testQuestQuestionByProject2() throws Exception {
        JSONArray quests = interfaceClient.queryQuestionByProject("431100-ac367ba398d744d489e9de4ed225b755");
        Map<String, Object> map = interfaceClient.queryQuestionByProject("431100-ac367ba398d744d489e9de4ed225b755", true);
        System.out.println(quests.toString());
        System.out.println(map.toString());
        //Map<String, Double> mm = importProjectService.gatherQuestScoreBySubject(quests, optionalQuestMap);
        System.out.println(importProjectService.separateSubject("004005006").toString());
//        System.out.println(mm);
    }

    @Test
    public void testQueryExamSchoolByProject() throws Exception {
        JSONArray schools = interfaceClient.queryExamSchoolByProject(PROJECT_ID, false);
        assertNotNull(schools);
        assertFalse(schools.isEmpty());
    }

    @Test
    public void testQueryExamClassByProject() throws Exception {
        JSONArray classes = interfaceClient.queryExamClassByProject("430100-501b96776dc348748e2afdb95d491516", "ad364e60-20ce-4063-a67e-beeb438d57e9", false);
        System.out.println(classes.toString());
        assertNotNull(classes);
        assertFalse(classes.isEmpty());
    }

    @Test
    public void testQueryClassExamStudent() throws Exception {
        JSONArray students = interfaceClient.queryClassExamStudent("430100-501b96776dc348748e2afdb95d491516", "006832bd-1ce7-477d-9a49-7d401aa38505");
        System.out.println(students.toJSONString());
        assertNotNull(students);
        assertFalse(students.isEmpty());
    }

    @Test
    public void testQuerySubjectListByProjectId() throws Exception {
        JSONArray subjects = interfaceClient.querySubjectListByProjectId("430100-d9463d7e76834ef69741bc77e631f282");
        System.out.println(subjects.toString());
        assertNotNull(subjects);
        assertFalse(subjects.isEmpty());
    }

    @Test
    public void testQueryProjectById() throws Exception {
        JSONObject project = interfaceClient.queryProjectById("430300-32d8433951ce43cab5883abff77c8ea3");
        System.out.println(project.toString());
        assertNotNull(project);
        assertEquals(PROJECT_ID, project.getString("id"));
        assertEquals(8, project.getIntValue("id"));
    }

    @Test
    public void testQueryProjectReportConfig() throws Exception {
        ApiResponse result = interfaceClient.queryProjectReportConfig("431100-ac367ba398d744d489e9de4ed225b755");
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

    @Test
    public void testCompareSubjectId() throws Exception {
/*        String projectId = "430100-2c641a3e36ff492aa535da7fb4cf28cf";
        JSONArray questJson = interfaceClient.queryQuestionByProject(projectId);
        JSONArray subjectJson = interfaceClient.querySubjectListByProjectId(projectId);
        ApiResponse projectConfigJson = interfaceClient.queryProjectReportConfig(projectId);
        System.out.println(projectConfigJson.getData());
        Map<String, Double> questMap = importProjectService.gatherQuestScoreBySubject(questJson, optionalQuestMap);
        List<String> subjectInQuest = new ArrayList<>(questMap.keySet());
        System.out.println("题目的科目类型-->" + subjectInQuest.toString());
        List<String> subjectIds = subjectJson.stream().map(subject -> {
            JSONObject s = (JSONObject) subject;
            return s.getString("subjectId");
        }).collect(Collectors.toList());
        System.out.println("科目ID-->" + subjectIds.toString());*/
    }

    @Test
    public void testImportSlicedSubject() throws Exception {

        String projectId = "431100-ac367ba398d744d489e9de4ed225b755";

        JSONArray jsonArray = interfaceClient.querySubjectListByProjectId(projectId);

        System.out.println(jsonArray.toString());
        //查询题目信息
        JSONArray jsonQuest = interfaceClient.queryQuestionByProject(projectId);
        System.out.println("试题：" + jsonQuest.toString());
        //获取选做题
        Map<String, Object> optionalQuestMap = interfaceClient.queryQuestionByProject(projectId, true);
        System.out.println("选做题：" + optionalQuestMap.toString());
        //统计出每个考试的总分
        Map<String, Double> subjectScore = importProjectService.gatherQuestScoreBySubject(jsonQuest, optionalQuestMap);

        System.out.println(subjectScore);
    }

}