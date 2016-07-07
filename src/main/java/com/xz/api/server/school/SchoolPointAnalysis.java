package com.xz.api.server.school;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.api.Param;
import com.xz.api.annotation.Function;
import com.xz.api.annotation.Parameter;
import com.xz.api.annotation.Type;
import com.xz.api.server.Server;
import com.xz.api.server.classes.ClassPointAnalysis;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.services.ClassService;
import com.xz.services.ScoreService;
import com.xz.services.StudentService;
import com.xz.services.SubjectService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author by fengye on 2016/7/5.
 */
@Function(description = "学校成绩-知识点分析", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "subjectId", type = Type.String, description = "科目id,默认第一个科目", required = false),
        @Parameter(name = "schoolId", type = Type.String, description = "学校ID", required = true)
})
@Service
public class SchoolPointAnalysis implements Server{
    @Autowired
    SubjectService subjectService;

    @Autowired
    ScoreService scoreService;

    @Autowired
    StudentService studentService;

    @Autowired
    ClassService classService;

    @Autowired
    ClassPointAnalysis classPointAnalysis;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String subjectId = param.getString("subjectId");
        String schoolId = param.getString("schoolId");

        // 初始化科目id
        if (StringUtil.isBlank(subjectId)) {
            subjectId = ClassPointAnalysis.initSubject(projectId, subjectService);
        }

        if (StringUtil.isBlank(subjectId)) {
            return Result.fail("找不到考试科目信息");
        }
        //查询学校学生，按科目总分从高到低排序
        Range schoolRange = Range.school(schoolId);

        List<Map<String, Object>> schoolPointAnalysis = classPointAnalysis.getPointStats(projectId, subjectId, schoolRange);
        List<Map<String, Object>> studentPointAnalysis = getStudentPointAnalysis(projectId, subjectId, schoolRange);

        return Result.success().set("schools", schoolPointAnalysis).set("students", studentPointAnalysis);

    }

    private List<Map<String,Object>> getStudentPointAnalysis(String projectId, String subjectId, Range schoolRange) {
        List<Map<String, Object>> list = new ArrayList<>();
        List<Document> studentList = studentService.getStudentList(projectId, schoolRange);
        for (Document student : studentList) {
            Map<String, Object> map = new HashMap<>();

            String className = classService.getClassName(projectId, student.getString("class"));
            String studentId = student.getString("student");
            map.put("studentId", studentId);
            map.put("studentName", student.getString("name"));
            map.put("className", className);

            Range range = Range.student(studentId);
            map.put("subjectScore", scoreService.getScore(projectId, range, Target.subject(subjectId)));
            map.put("pointStats", classPointAnalysis.getPointStats(projectId, subjectId, range));
            list.add(map);
        }

        list.sort((o1, o2) -> ((Double) o2.get("subjectScore")).compareTo(((Double) o1.get("subjectScore"))));
        return list;
    }
}
