package com.xz.api.server.school;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.api.annotation.Function;
import com.xz.api.annotation.Parameter;
import com.xz.api.annotation.Type;
import com.xz.api.server.Server;
import com.xz.api.server.classes.ClassQuestScoreDetailAnalysis;
import com.xz.bean.Range;
import com.xz.services.*;
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
@SuppressWarnings("unchecked")
@Function(description = "学校成绩-学生题目得分明细", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "schoolId", type = Type.String, description = "学校ID", required = true),
        @Parameter(name = "subjectId", type = Type.String, description = "考试科目id", required = true)
})
@Service
public class SchoolQuestScoreDetailAnalysis implements Server {
    public static Logger LOG = LoggerFactory.getLogger(SchoolQuestScoreDetailAnalysis.class);

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
        String schoolId = param.getString("schoolId");
        String subjectId = param.getString("subjectId");

        List<Map<String, Object>> questList = classQuestScoreDetailAnalysis.getQuestListBySubject(projectId, subjectId, null);
        List<Map<String, Object>> studentList = getStudentBySchoolId(projectId, subjectId, schoolId);

        return Result.success().set("questList", questList).set("studentList", studentList);
    }

    private List<Map<String,Object>> getStudentBySchoolId(String projectId, String subjectId, String schoolId) {
        List<Map<String, Object>> students = new ArrayList<>();
        List<Document> studentList = studentService.getStudentList(projectId, Range.school(schoolId));
        for(Document student : studentList){
            String schoolName = schoolService.getSchoolName(projectId, schoolId);
            String className = classService.getClassName(projectId, student.getString("class"));
            Map<String, Object> studentMap = new HashMap<>();
            studentMap.put("quests", classQuestScoreDetailAnalysis.getQuestListBySubject(projectId, subjectId, student.getString("student")));
            studentMap.put("studentName", student.getString("name"));
            studentMap.put("className", className);
            studentMap.put("schoolName", schoolName);
            studentMap.put("studentId", student.getString("student"));
            students.add(studentMap);
        }
        return students;
    }
}
