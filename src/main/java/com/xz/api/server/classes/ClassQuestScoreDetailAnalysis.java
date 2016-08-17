package com.xz.api.server.classes;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.mongo.QuestNoComparator;
import com.xz.api.Param;
import com.xz.api.annotation.Function;
import com.xz.api.annotation.Parameter;
import com.xz.api.annotation.Type;
import com.xz.api.server.Server;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.services.*;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author by fengye on 2016/7/1.
 */
@Function(description = "班级成绩-学生题目得分明细", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "schoolId", type = Type.String, description = "学校id", required = true),
        @Parameter(name = "subjectId", type = Type.String, description = "考试科目id", required = true),
        @Parameter(name = "classId", type = Type.String, description = "班级id", required = true)

})
@Service
public class ClassQuestScoreDetailAnalysis implements Server {
    public static Logger LOG = LoggerFactory.getLogger(ClassQuestScoreDetailAnalysis.class);

    public static final Comparator<Document> QUEST_NO_COMPARATOR = new QuestNoComparator();

    @Autowired
    StudentService studentService;

    @Autowired
    ScoreService scoreService;

    @Autowired
    ClassService classService;

    @Autowired
    SubjectService subjectService;

    @Autowired
    QuestService questService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String classId = param.getString("classId");
        String subjectId = param.getString("subjectId");

        List<Map<String, Object>> questList = getQuestListBySubject(projectId, subjectId, null);
        List<Map<String, Object>> studentList = getStudentByClassId(projectId, subjectId, classId);

        return Result.success().set("questList", questList).set("studentList", studentList);
    }

    public List<Map<String,Object>> getQuestListBySubject(String projectId, String subjectId, String studentId) {
        List<Map<String, Object>> quests = new ArrayList<>();
        //获取考试题目列表
        List<Document> questList = questService.getQuests(projectId, subjectId);
        //按照题号排序
        Collections.sort(questList, QUEST_NO_COMPARATOR);
        for(Document quest : questList){
            Map<String, Object> questMap = new HashMap<>();
            //获取题目得分
            if(null == studentId){
                //获取试题的分数
                questMap.put("score", quest.get("score"));
            }else{
                //获取题目的得分
                double score = scoreService.getScore(projectId, Range.student(studentId), Target.quest(quest.getString("questId")));
                questMap.put("score", score);
            }
            questMap.put("questNo", quest.getString("questNo"));
            questMap.put("questionTypeName", quest.getString("questionTypeName"));
            questMap.put("isObjective", quest.get("isObjective"));
            quests.add(questMap);
        }
        return quests;
    }

    private List<Map<String,Object>> getStudentByClassId(String projectId, String subjectId, String classId) {
        List<Map<String, Object>> students = new ArrayList<>();
        List<Document> studentList = studentService.getStudentList(projectId, Range.clazz(classId));
        for(Document student : studentList){
            String className = classService.getClassName(projectId, classId);
            Map<String, Object> studentMap = new HashMap<>();
            studentMap.put("quests", getQuestListBySubject(projectId, subjectId, student.getString("student")));
            studentMap.put("studentName", student.getString("name"));
            studentMap.put("examNo", student.getString("examNo"));
            studentMap.put("className", className);
            studentMap.put("studentId", student.getString("student"));
            students.add(studentMap);
        }
        return students;
    }
}
