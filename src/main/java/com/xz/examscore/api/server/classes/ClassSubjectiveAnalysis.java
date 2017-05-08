package com.xz.examscore.api.server.classes;

/**
 * @author by fengye on 2016/9/19.
 */

import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.ajiaedu.common.mongo.DocumentUtils;
import com.xz.ajiaedu.common.mongo.QuestNoComparator;
import com.xz.ajiaedu.common.ajia.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.bean.Range;
import com.xz.examscore.services.*;
import org.apache.commons.collections.MapUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.xz.examscore.api.server.classes.ClassPointAnalysis.initSubject;

/**
 * 学校成绩-主观题分析
 *
 * @author zhaorenwu
 */
@Function(description = "班级成绩-主观题分析", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "subjectId", type = Type.String, description = "科目ID", required = true),
        @Parameter(name = "classId", type = Type.String, description = "班级ID", required = true),
        @Parameter(name = "authSubjectIds", type = Type.StringArray, description = "可访问科目范围，为空返回所有", required = false)
})
@Service
public class ClassSubjectiveAnalysis implements Server {
    @Autowired
    ClassService classService;

    @Autowired
    QuestService questService;

    @Autowired
    ScoreService scoreService;

    @Autowired
    StudentService studentService;

    @Autowired
    SubjectService subjectService;

    public static final Comparator<Document> QUEST_NO_COMPARATOR = new QuestNoComparator();

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

        List<Document> questDocs = questService.getQuests(projectId, subjectId, false);
        Collections.sort(questDocs, QUEST_NO_COMPARATOR);

        List<Map<String, Object>> quests = new ArrayList<>();
        questDocs.forEach(
                quest -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("questNo", quest.getString("questNo"));
                    item.put("score", quest.getDouble("score"));
                    quests.add(item);
                }
        );

        List<Map<String, Object>> classSubjectiveList
                = getClassSubjectiveData(projectId, classId, questDocs);
        return Result.success()
                .set("subjectiveQuests", quests)
                .set("classSubjectiveList", classSubjectiveList)
                .set("hasHeader", !quests.isEmpty() && !classSubjectiveList.isEmpty());
    }

    private List<Map<String, Object>> getClassSubjectiveData(String projectId, String classId, List<Document> quests) {
        List<Document> studentDocs = studentService.getStudentList(projectId, Range.clazz(classId));
        List<Map<String, Object>> studentList = new ArrayList<>();
        studentDocs.forEach(studentDoc -> {
            String studentId = studentDoc.getString("student");
            String studentName = studentDoc.getString("name");
            List<Map<String, Object>> subjectiveQuests = getSubjectiveQuests(projectId, studentId, quests);
            Map<String, Object> studentMap = new HashMap<>();
            studentMap.put("studentId", studentId);
            studentMap.put("studentName", studentName);
            studentMap.put("subjectiveQuests", subjectiveQuests);
            studentList.add(studentMap);
        });
        return studentList;
    }

    //获取学生主观题信息
    private List<Map<String, Object>> getSubjectiveQuests(String projectId, String studentId, List<Document> quests) {
        List<Map<String, Object>> subjectiveQuests = new ArrayList<>();
        for (Map<String, Object> quest : quests) {
            Document scoreDoc = scoreService.getScoreDoc(projectId, studentId, MapUtils.getString(quest, "questId"), false);
            String questNo = MapUtils.getString(quest, "questNo");
            //学生得分
            double score = DocumentUtils.getDouble(scoreDoc, "score", 0);
            //题目分值
            double fullScore = MapUtils.getDouble(quest, "score");
            List<String> urls = getUrlValues(scoreDoc);
            Map<String, Object> subjectiveQuest = new HashMap<>();
            subjectiveQuest.put("questNo", questNo);
            subjectiveQuest.put("score", score);
            subjectiveQuest.put("fullScore", fullScore);
            subjectiveQuest.put("url", urls);
            subjectiveQuests.add(subjectiveQuest);
        }
        return subjectiveQuests;
    }

    private List<String> getUrlValues(Document scoreDoc) {
        if (null != scoreDoc && !scoreDoc.isEmpty()) {
            Document urlDoc = (Document) scoreDoc.get("url");
            if (null != urlDoc && !urlDoc.isEmpty()) {
                Collection col = urlDoc.values();
                return new ArrayList<>(col);
            }
        }
        return Collections.emptyList();
    }
}
