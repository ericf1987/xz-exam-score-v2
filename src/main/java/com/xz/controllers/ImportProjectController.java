package com.xz.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xz.ajiaedu.common.beans.exam.ExamProject;
import com.xz.ajiaedu.common.beans.user.School;
import com.xz.ajiaedu.common.lang.Context;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.bean.Target;
import com.xz.intclient.InterfaceClient;
import com.xz.services.FullScoreService;
import com.xz.services.ProjectService;
import com.xz.services.SchoolService;
import com.xz.services.SubjectService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;

/**
 * 湘潭联考：430300-672a0ed23d9148e5a2a31c8bf1e08e62
 *
 * @author yiding_he
 */
@Controller
@RequestMapping("/project")
public class ImportProjectController {

    @Autowired
    InterfaceClient interfaceClient;

    @Autowired
    ProjectService projectService;

    @Autowired
    SchoolService schoolService;

    @Autowired
    SubjectService subjectService;

    @Autowired
    FullScoreService fullScoreService;

    @RequestMapping(value = "import", method = RequestMethod.POST)
    @ResponseBody
    public Result importProject(
            @RequestParam("project") String projectId) {

        Context context = new Context();

        // 下面的导入顺序不能变更，否则可能造成数据错误
        importProjectInfo(projectId);
        importSubjects(projectId);
        importQuests(projectId);
        importSchools(projectId, context);
        importStudents(projectId, context);

        return Result.success();
    }

    private void importStudents(String projectId, Context context) {
        List<Document> schools = context.get("schools");

    }

    private void importSchools(String projectId, Context context) {
        Param param = new Param().setParameter("projectId", projectId);
        Result result = interfaceClient.request("QueryExamSchoolByProject", param);
        JSONArray jsonArray = result.get("schools");

        List<Document> schoolList = new ArrayList<>();  // 存入 project_list

        jsonArray.forEach(o -> {
            JSONObject schoolObj = (JSONObject) o;
            School school = new School();
            school.setId(schoolObj.getString("id"));
            school.setName(schoolObj.getString("name"));
            school.setArea(schoolObj.getString("area"));
            school.setCity(schoolObj.getString("city"));
            school.setProvince(schoolObj.getString("province"));

            schoolService.saveProjectSchool(projectId, school);
            schoolList.add(doc("school", school.getId()).append("name", school.getName()));
        });

        context.put("schools", schoolList);  // 导入学生时要用到
        projectService.saveProjectSchools(projectId, schoolList);
    }

    private void importQuests(String projectId) {
        Param param = new Param().setParameter("projectId", projectId);
        Result result = interfaceClient.request("QueryQuestionByProject", param);

        JSONArray jsonArray = result.get("quests");
        jsonArray.forEach(o -> {
            JSONObject questObj = (JSONObject) o;

        });
    }

    private void importSubjects(String projectId) {
        Param param = new Param().setParameter("projectId", projectId);
        Result result = interfaceClient.request("QuerySubjectListByProjectId", param);

        JSONArray jsonArray = result.get("result");
        List<String> subjects = new ArrayList<>();
        jsonArray.forEach(o -> {
            JSONObject subjectObj = (JSONObject) o;
            String subjectId = subjectObj.getString("subjectId");
            Double fullScore = subjectObj.getDouble("totalScore");

            subjects.add(subjectId);
            fullScoreService.saveFullScore(projectId, Target.subject(subjectId), fullScore);
        });

        subjectService.saveProjectSubjects(projectId, subjects);
    }

    private void importProjectInfo(String projectId) {
        Param param = new Param().setParameter("projectId", projectId);
        Result result = interfaceClient.request("QueryProjectById", param);

        JSONObject projectObj = result.get("result");
        ExamProject project = new ExamProject();
        project.setId(projectId);
        project.setName(projectObj.getString("name"));
        project.setGrade(projectObj.getInteger("grade"));

        projectService.saveProject(project);
    }
}
