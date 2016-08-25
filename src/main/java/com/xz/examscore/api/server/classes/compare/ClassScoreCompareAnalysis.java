package com.xz.examscore.api.server.classes.compare;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.*;
import com.xz.examscore.util.DoubleUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author by fengye on 2016/7/26.
 */

@Function(description = "班级成绩-分数对比", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "subjectId", type = Type.String, description = "科目id,默认第一个科目", required = false),
        @Parameter(name = "classId", type = Type.String, description = "班级id", required = true)
})
@Service
public class ClassScoreCompareAnalysis implements Server{

    @Autowired
    ProjectService projectService;

    @Autowired
    StudentService studentService;

    @Autowired
    ScoreService scoreService;

    @Autowired
    TargetService targetService;

    @Autowired
    SubjectService subjectService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String subjectId = param.getString("subjectId");
        String classId = param.getString("classId");

        List<Document> projectList = projectService.listProjectsByRange(Range.clazz(classId));

        List<Map<String, Object>> studentList = getStudentList(projectId, subjectId, classId, projectList);

        return Result.success().set("projectList", projectList).set("studentList", studentList).set("hasHeader", !projectList.isEmpty());
    }

    private List<Map<String, Object>> getStudentList(String projectId, String subjectId, String classId, List<Document> projectList) {
        Target target = targetService.getTarget(projectId, subjectId);
        List<Document> students = studentService.getStudentList(projectId, Range.clazz(classId));
        List<Map<String, Object>> studentList = new ArrayList<>();
        for (Document student : students){
            List<Map<String, Object>> scores = new ArrayList<>();
            Map<String, Object> studentMap = new HashMap<>();
            studentMap.put("studentName", student.getString("name"));
            studentMap.put("studentId", student.getString("student"));
            for (Document project : projectList){
                String currentProject = project.getString("project");
                double score = scoreService.getScore(currentProject, Range.student(student.getString("student")), target);
                scores.add(getScoreMap(currentProject, score));
            }
            studentMap.put("scores", scores);
            studentList.add(studentMap);
        }
        return studentList;
    }

    private Map<String, Object> getScoreMap(String project, double score) {
        Map<String, Object> map = new HashMap<>();
        map.put("projectId", project);
        map.put("score", DoubleUtils.round(score, false));
        return map;
    }
}
