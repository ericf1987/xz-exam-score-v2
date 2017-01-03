package com.xz.examscore.api.server.project;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.api.server.classes.ClassQuestScoreDetailAnalysis;
import com.xz.examscore.bean.Range;
import com.xz.examscore.services.*;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author by fengye on 2016/7/1.
 */
@Function(description = "项目成绩-学生题目得分明细", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "subjectId", type = Type.String, description = "考试科目id", required = true)
})
@Service
public class ProjectQuestScoreDetailAnalysis implements Server{
    public static Logger LOG = LoggerFactory.getLogger(ProjectQuestScoreDetailAnalysis.class);

    @Autowired
    ClassQuestScoreDetailAnalysis classQuestScoreDetailAnalysis;

    @Autowired
    StudentService studentService;

    @Autowired
    ScoreService scoreService;

    @Autowired
    SchoolService schoolService;

    @Autowired
    ClassService classService;

    @Autowired
    SubjectService subjectService;

    @Autowired
    QuestService questService;
    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String subjectId = param.getString("subjectId");

        List<Map<String, Object>> questList = classQuestScoreDetailAnalysis.getQuestListBySubject(projectId, subjectId, null);
        List<Map<String, Object>> studentList = getStudentBySchoolId(projectId, subjectId);

        return Result.success().set("questList", questList).set("studentList", studentList);
    }

    private List<Map<String,Object>> getStudentBySchoolId(String projectId, String subjectId) {
        List<Map<String, Object>> students = new ArrayList<>();
        //获取所有参考学校
        List<Document> schools = schoolService.getProjectSchools(projectId);
        for(Document school : schools){
            String schoolName = school.getString("name");
            String schoolId = school.getString("school");
            List<Document> studentList = studentService.getStudentList(projectId, Range.school(schoolId));
            for(Document student : studentList){
                String className = classService.getClassName(projectId, student.getString("class"));
                Map<String, Object> studentMap = new HashMap<>();
                studentMap.put("quests", classQuestScoreDetailAnalysis.getQuestListBySubject(projectId, subjectId, student.getString("student")));
                studentMap.put("studentName", student.getString("name"));
                studentMap.put("examNo", student.getString("examNo"));
                studentMap.put("customExamNo", student.getString("customExamNo"));
                studentMap.put("className", className);
                studentMap.put("schoolName", schoolName);
                studentMap.put("studentId", student.getString("student"));
                students.add(studentMap);
            }
        }
        return students;
    }
}
