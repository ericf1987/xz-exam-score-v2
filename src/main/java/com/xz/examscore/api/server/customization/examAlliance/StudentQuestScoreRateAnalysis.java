package com.xz.examscore.api.server.customization.examAlliance;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.mongo.DocumentUtils;
import com.xz.ajiaedu.common.mongo.QuestNoComparator;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.api.server.classes.ClassQuestScoreDetailAnalysis;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.*;
import com.xz.examscore.util.DoubleUtils;
import org.apache.commons.collections.MapUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author by fengye on 2017/4/10.
 */
@Function(description = "联考项目-学生各小题得分率", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "schoolId", type = Type.String, description = "学校ID", required = true),
        @Parameter(name = "subjectId", type = Type.String, description = "科目ID", required = true)
})
@Service
public class StudentQuestScoreRateAnalysis implements Server {
    public static Logger LOG = LoggerFactory.getLogger(StudentQuestScoreRateAnalysis.class);

    public static final Comparator<Document> QUEST_NO_COMPARATOR = new QuestNoComparator();


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

    @Autowired
    ClassQuestScoreDetailAnalysis classQuestScoreDetailAnalysis;

    @Autowired
    FullScoreService fullScoreService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String schoolId = param.getString("schoolId");
        String subjectId = param.getString("subjectId");

        List<Map<String, Object>> quests = getQuestList(projectId, subjectId);
        List<Map<String, Object>> studentList = getStudentBySchoolId(projectId, schoolId, quests);

        return Result.success().set("questList", quests).set("studentList", studentList);
    }

    private List<Map<String, Object>> getQuestList(String projectId, String subjectId) {
        List<Document> quests = questService.getQuests(projectId, subjectId);

        Collections.sort(quests, QUEST_NO_COMPARATOR);

        return quests.stream().map(q -> {
            Map<String, Object> questMap = new HashMap<>();
            questMap.put("questId", DocumentUtils.getString(q, "questId", ""));
            questMap.put("questNo", DocumentUtils.getString(q, "questNo", ""));
            questMap.put("isObjective", MapUtils.getBoolean(q, "isObjective"));
            questMap.put("fullScore", DocumentUtils.getDouble(q, "score", 0));
            return questMap;
        }).collect(Collectors.toList());
    }

    protected List<Map<String, Object>> getStudentBySchoolId(String projectId, String schoolId, List<Map<String, Object>> quests) {
        List<Map<String, Object>> students = new ArrayList<>();
        List<Document> studentList = studentService.getStudentList(projectId, Range.school(schoolId));
        for (Document student : studentList) {
            String schoolName = schoolService.getSchoolName(projectId, schoolId);
            String className = classService.getClassName(projectId, student.getString("class"));
            Map<String, Object> studentMap = new HashMap<>();
            studentMap.put("quests", fillScore(projectId, student.getString("student"), quests));
            studentMap.put("studentName", student.getString("name"));
            studentMap.put("examNo", student.getString("examNo"));
            studentMap.put("customExamNo", student.getString("customExamNo"));
            studentMap.put("className", className);
            studentMap.put("schoolName", schoolName);
            studentMap.put("studentId", student.getString("student"));
            students.add(studentMap);
        }
        return students;
    }

    /**
     * 填充得分和得分率
     *
     * @param projectId 项目ID
     * @param student   学生ID
     * @param quests    题目列表哦
     * @return 返回结果
     */
    private List<Map<String, Object>> fillScore(String projectId, String student, List<Map<String, Object>> quests) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> questMap : quests) {
            String questId = MapUtils.getString(questMap, "questId", "");
            double fullScore = MapUtils.getDouble(questMap, "fullScore", 0d);
            Target questTarget = Target.quest(questId);
            double score = scoreService.getScore(projectId, Range.student(student), questTarget);
            double rate = fullScore == 0 ? 0 : DoubleUtils.round(score / fullScore, true);
            Map<String, Object> newMap = new HashMap<>(questMap);
            newMap.put("score", score);
            newMap.put("rate", rate);
            result.add(newMap);
        }
        return result;
    }

}
