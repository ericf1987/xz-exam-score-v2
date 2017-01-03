package com.xz.examscore.api.server.classes;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.api.server.project.ProjectQuestTypeAnalysis;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.*;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xz.examscore.api.server.classes.ClassPointAnalysis.initSubject;

/**
 * 班级成绩-试卷题型分析
 *
 * @author zhaorenwu
 */
@Function(description = "班级成绩-试卷题型分析", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "subjectId", type = Type.String, description = "科目ID", required = false),
        @Parameter(name = "classId", type = Type.String, description = "班级id", required = true),
        @Parameter(name = "authSubjectIds", type = Type.StringArray, description = "可访问科目范围，为空返回所有", required = false)
})
@Service
public class ClassQuestTypeAnalysis implements Server {

    @Autowired
    StudentService studentService;

    @Autowired
    QuestTypeService questTypeService;

    @Autowired
    QuestTypeScoreService questTypeScoreService;

    @Autowired
    FullScoreService fullScoreService;

    @Autowired
    ScoreService scoreService;

    @Autowired
    SubjectService subjectService;

    @Autowired
    TargetService targetService;

    @Autowired
    ProjectQuestTypeAnalysis projectQuestTypeAnalysis;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String subjectId = param.getString("subjectId");
        String classId = param.getString("classId");
        String[] authSubjectIds = param.getStringValues("authSubjectIds");

        // 初始化科目id
        if (StringUtil.isBlank(subjectId)) {
            subjectId = initSubject(projectId, authSubjectIds, subjectService);
        }

        if (StringUtil.isBlank(subjectId)) {
            return Result.fail("找不到考试科目信息");
        }

        List<Map<String, Object>> classQuestTypeAnalysis = getClassQuestTypeAnalysis(projectId, subjectId, classId);
        List<Map<String, Object>> studentQuestTypeAnalysis = getStudentQuestTypeAnalysis(projectId, subjectId, classId);

        return Result.success()
                .set("classes", classQuestTypeAnalysis)
                .set("students", studentQuestTypeAnalysis)
                .set("hasHeader", !(classQuestTypeAnalysis.isEmpty() || studentQuestTypeAnalysis.isEmpty()));
    }

    // 学生试题分析
    private List<Map<String, Object>> getStudentQuestTypeAnalysis(String projectId, String subjectId, String classId) {
        List<Map<String, Object>> list = new ArrayList<>();

        List<Document> studentList = studentService.getStudentList(projectId, Range.clazz(classId));
        for (Document student : studentList) {
            Map<String, Object> map = new HashMap<>();

            String studentId = student.getString("student");
            String studentName = student.getString("name");
            map.put("examNo", student.getString("examNo"));
            map.put("customExamNo", student.getString("customExamNo"));
            map.put("studentId", studentId);
            map.put("studentName", studentName);

            Range range = Range.student(studentId);
            double score = scoreService.getScore(projectId, range, Target.subject(subjectId));
            map.put("score", score);
            //判断学生是否缺考
            Target target = targetService.getTarget(projectId, subjectId);
            boolean isAbsent = scoreService.isStudentAbsent(projectId, studentId, target);
            if (isAbsent) map.put("isAbsent", isAbsent);
            List<Map<String, Object>> questTypes = projectQuestTypeAnalysis.getQuestTypeAnalysis(projectId, subjectId, range);
            map.put("questTypes", questTypes);

            list.add(map);
        }

        list.sort((o1, o2) -> ((Double) o2.get("score")).compareTo(((Double) o1.get("score"))));
        return list;
    }

    // 班级试题分析
    private List<Map<String, Object>> getClassQuestTypeAnalysis(String projectId, String subjectId, String classId) {
        Range range = Range.clazz(classId);
        return projectQuestTypeAnalysis.getQuestTypeAnalysis(projectId, subjectId, range);
    }
}
