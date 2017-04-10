package com.xz.examscore.services;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xz.ajiaedu.common.beans.exam.ExamProject;
import com.xz.ajiaedu.common.lang.Context;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.intclient.InterfaceClient;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.Map;

/**
 * (description)
 * created at 16/07/08
 *
 * @author yiding_he
 */
public class ImportProjectServiceTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    ImportProjectService importProjectService;

    @Autowired
    InterfaceClient interfaceClient;

    @Autowired
    ProjectConfigService projectConfigService;

    @Autowired
    ProjectService projectService;

    @Test
    public void testImportProjectQuest() throws Exception {
        importProjectService.importProject("430100-d9463d7e76834ef69741bc77e631f282", false);
    }

    @Test
    public void testImportReportConfig() throws Exception {
        Context context = new Context();
        importProjectService.importProjectReportConfig("430100-d9463d7e76834ef69741bc77e631f282", context);
        System.out.println(context.toString());
    }

    @Test
    public void testImportProjectInfo() throws Exception {
        importProjectService.importProjectInfo("430300-32d8433951ce43cab5883abff77c8ea3", new Context());
    }

    @Test
    public void testimportProject() throws Exception{
        importProjectService.importProject("430300-f529e0f0236d49559b0c27acbbb255ed", true);
    }

    @Test
    public void testgetOptionalQuestNo() throws Exception{
        String projectId = "430300-c582131e66b64fe38da7d0510c399ec4";
        Map<String, Object> map = interfaceClient.queryQuestionByProject(projectId, true);
        Map<String, Object> optionalQuestNo = importProjectService.getOptionalQuestNo(map);
        System.out.println("选做题->" + optionalQuestNo.toString());
        JSONArray jsonArr = interfaceClient.queryQuestionByProject(projectId);
        System.out.println("总分信息->" + importProjectService.gatherQuestScoreBySubject(jsonArr, map));
    }

    @Test
    public void testimportSubjects() throws Exception {
        String projectId = "431100-ac367ba398d744d489e9de4ed225b755";
        Context context = new Context();
        JSONObject projectObj = interfaceClient.queryProjectById(projectId);

        ExamProject project = new ExamProject();
        project.setId(projectId);
        project.setName(projectObj.getString("name"));
        project.setGrade(projectObj.getInteger("grade"));
        project.setCreateTime(new Date());
        //项目类型 文科：W，理科：L
        project.setCategory(projectObj.getString("category"));
        //考试开始日期
        project.setExamStartDate(projectObj.getString("examStartDate"));

        context.put("project", project);
        projectService.saveProject(project);

        context.put("projectConfig", projectConfigService.getProjectConfig(projectId));

        importProjectService.importSubjects(projectId, context);
    }

}