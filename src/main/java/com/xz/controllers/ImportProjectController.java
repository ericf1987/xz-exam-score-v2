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
import com.xz.services.*;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
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

    static final Logger LOG = LoggerFactory.getLogger(ImportProjectController.class);

    @Autowired
    InterfaceClient interfaceClient;

    @Autowired
    ProjectService projectService;

    @Autowired
    SchoolService schoolService;

    @Autowired
    ClassService classService;

    @Autowired
    StudentService studentService;

    @Autowired
    SubjectService subjectService;

    @Autowired
    FullScoreService fullScoreService;

    @RequestMapping(value = "import", method = RequestMethod.POST)
    @ResponseBody
    public Result importProject(
            @RequestParam("project") String projectId) {

        LOG.info("开始导入项目 " + projectId + " 基本信息...");
        Context context = new Context();

        // 下面的导入顺序不能变更，否则可能造成数据错误
        importProjectInfo(projectId, context);
        importSubjects(projectId);
        importQuests(projectId);
        importSchools(projectId, context);
        importClasses(projectId, context);
        importStudents(projectId, context);

        LOG.info("项目 " + projectId + " 基本信息导入完毕。");
        return Result.success();
    }

    private void importClasses(String projectId, Context context) {
        List<Document> classes = new ArrayList<>();
        List<Document> schools = context.get("schools");
        ExamProject project = context.get("project");

        for (Document school : schools) {
            String schoolId = school.getString("school");
            List<Document> schoolClasses = new ArrayList<>();

            LOG.info("导入学校 " + schoolId + "(" + school.getString("name") + ") 班级信息...");

            Param param = new Param().setParameter("projectId", projectId).setParameter("schoolId", schoolId);
            Result result = interfaceClient.request("QueryExamClassByProject", param);
            JSONArray jsonArray = result.get("classes");

            jsonArray.forEach(o -> {
                JSONObject classObj = (JSONObject) o;
                Document schoolClass = new Document();
                schoolClass.append("class", classObj.getString("id"));
                schoolClass.append("name", classObj.getString("name"));
                schoolClass.append("grade", project.getGrade());
                schoolClass.append("school", schoolId);
                schoolClass.append("area", school.getString("area"));
                schoolClass.append("city", school.getString("city"));
                schoolClass.append("province", school.getString("province"));

                schoolClasses.add(schoolClass);
                classes.add(schoolClass);               // 之后查询学生用
            });

            classService.saveProjectSchoolClasses(projectId, schoolId, schoolClasses);
        }

        context.put("classes", classes);
    }

    private void importStudents(String projectId, Context context) {
        List<Document> classes = context.get("classes");
        int classCount = classes.size();
        int index = 0;

        for (Document classDoc : classes) {
            String classId = classDoc.getString("class");
            index++;
            LOG.info("导入班级 " + classId + " 的考生信息(" + index + "/" + classCount + ")...");

            List<Document> classStudents = new ArrayList<>();
            Param param = new Param().setParameter("projectId", projectId).setParameter("classId", classId);
            Result result = interfaceClient.request("QueryClassExamStudent", param);

            JSONArray students = result.get("examStudents");
            students.forEach(o -> {
                JSONObject studentObj = (JSONObject) o;
                Document studentDoc = new Document()
                        .append("project", projectId)
                        .append("student", studentObj.get("id"))
                        .append("name", studentObj.get("name"))
                        .append("class", classDoc.get("class"))
                        .append("school", classDoc.get("school"))
                        .append("area", classDoc.get("area"))
                        .append("city", classDoc.get("city"))
                        .append("province", classDoc.get("province"));

                classStudents.add(studentDoc);
            });

            studentService.saveProjectClassStudents(projectId, classId, classStudents);
        }
    }

    private void importSchools(String projectId, Context context) {
        LOG.info("导入项目 " + projectId + " 学校信息...");
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

        context.put("schools", schoolList);  // 导入班级时要用到
        projectService.updateProjectSchools(projectId, schoolList);
    }

    private void importQuests(String projectId) {
        LOG.info("导入项目 " + projectId + " 考题信息...");
        Param param = new Param().setParameter("projectId", projectId);
        Result result = interfaceClient.request("QueryQuestionByProject", param);

        JSONArray jsonArray = result.get("quests");
        jsonArray.forEach(o -> {
            JSONObject questObj = (JSONObject) o;

        });
    }

    private void importSubjects(String projectId) {
        LOG.info("导入项目 " + projectId + " 科目信息...");
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

    private void importProjectInfo(String projectId, Context context) {
        LOG.info("导入项目 " + projectId + " 基本信息...");
        Param param = new Param().setParameter("projectId", projectId);
        Result result = interfaceClient.request("QueryProjectById", param);

        JSONObject projectObj = result.get("result");
        ExamProject project = new ExamProject();
        project.setId(projectId);
        project.setName(projectObj.getString("name"));
        project.setGrade(projectObj.getInteger("grade"));
        project.setCreateTime(new Date());

        context.put("project", project);
        projectService.saveProject(project);
    }
}
