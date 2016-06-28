package com.xz.api.server.classes;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.api.Param;
import com.xz.api.annotation.Function;
import com.xz.api.annotation.Parameter;
import com.xz.api.annotation.Type;
import com.xz.api.server.Server;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.services.*;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xz.api.server.project.ProjectQuestTypeAnalysis.getQuestTypeAnalysis;

/**
 * 班级成绩-试卷题型分析
 *
 * @author zhaorenwu
 */
@Function(description = "班级成绩-试卷题型分析", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "subjectId", type = Type.String, description = "科目ID", required = false),
        @Parameter(name = "classId", type = Type.String, description = "班级id", required = true)
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

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String subjectId = param.getString("subjectId");
        String classId = param.getString("classId");

        // 初始化科目id
        if (StringUtil.isBlank(subjectId)) {
            subjectId = initSubject(projectId);
        }

        if (StringUtil.isBlank(subjectId)) {
            return Result.fail("找不到考试科目信息");
        }

        List<Map<String, Object>> classQuestTypeAnalysis = getClassQuestTypeAnalysis(projectId, subjectId, classId);
        List<Map<String, Object>> studentQuestTypeAnalysis = getStudentQuestTypeAnalysis(projectId, subjectId, classId);

        return Result.success().set("classes", classQuestTypeAnalysis).set("students", studentQuestTypeAnalysis);
    }

    private String initSubject(String projectId) {
        List<String> subjectIds = subjectService.querySubjects(projectId);
        subjectIds.sort(String::compareTo);

        if (!subjectIds.isEmpty()) {
            return subjectIds.get(0);
        }

        return null;
    }

    // 学生试题分析
    private List<Map<String, Object>> getStudentQuestTypeAnalysis(String projectId, String subjectId, String classId) {
        List<Map<String, Object>> list = new ArrayList<>();

        List<Document> studentList = studentService.getStudentList(projectId, Range.clazz(classId));
        for (Document student : studentList) {
            Map<String, Object> map = new HashMap<>();

            String studentId = student.getString("student");
            String studentName = student.getString("name");
            map.put("studentId", studentId);
            map.put("studentName", studentName);

            Range range = Range.student(studentId);
            double score = scoreService.getScore(projectId, range, Target.subject(subjectId));
            map.put("score", score);

            List<Map<String, Object>> questTypes = getQuestTypeAnalysis(projectId, subjectId, range,
                    questTypeService, fullScoreService, questTypeScoreService);
            map.put("questTypes", questTypes);

            list.add(map);
        }

        list.sort((o1, o2) -> ((Double) o2.get("score")).compareTo(((Double) o1.get("score"))));
        return list;
    }

    // 班级试题分析
    private List<Map<String, Object>> getClassQuestTypeAnalysis(String projectId, String subjectId, String classId) {
        Range range = Range.clazz(classId);
        return getQuestTypeAnalysis(projectId, subjectId, range,
                questTypeService, fullScoreService, questTypeScoreService);
    }
}
