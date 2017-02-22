package com.xz.examscore.asynccomponents.report.biz.classes;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.api.server.classes.ClassQuestScoreDetailAnalysis;
import com.xz.examscore.bean.Range;
import com.xz.examscore.services.*;
import org.apache.commons.collections.MapUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author by fengye on 2017/2/20.
 */
public class ClassQuestScoreDetailBiz implements Server{

    @Autowired
    SchoolService schoolService;

    @Autowired
    ClassService classService;

    @Autowired
    StudentService studentService;

    @Autowired
    ScoreService scoreService;

    @Autowired
    QuestService questService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String classId = param.getString("classId");
        String subjectId = param.getString("subjectId");

        return getResultData(projectId, Range.clazz(classId), subjectId);
    }

    protected Result getResultData(String projectId, Range range, String subjectId) {

        //参考学生列表
        List<String> studentIds = studentService.getStudentIds(projectId, subjectId, range);

        //科目试题列表
        List<Document> questList = questService.getQuests(projectId, subjectId);

        //按照题号排序
        Collections.sort(questList, ClassQuestScoreDetailAnalysis.QUEST_NO_COMPARATOR);

        //基础数据信息
        List<Map<String, Object>> basicInfo = packBasicInfo(projectId, studentIds);

        //试题得分信息
        List<Document> questScoreDoc = packQuestDetail(projectId, subjectId);

        List<Map<String, Object>> resultData = packResult(basicInfo, questList, questScoreDoc);

        List<Map<String, Object>> quests = questList.stream().map(this::getQuestScoreMap).collect(Collectors.toList());

        return Result.success().set("questList", quests).set("studentList", resultData);
    }

    protected List<Map<String, Object>> packResult(List<Map<String, Object>> basicInfo, List<Document> questList, List<Document> questScoreDoc) {
        for (Map<String, Object> map : basicInfo) {
            String studentId = MapUtils.getString(map, "student");

            List<Map<String, Object>> quests = new ArrayList<>();
            for (Document quest : questList) {
                String questId = quest.getString("questId");
                List<Map<String, Object>> questDoc = questScoreDoc.stream().filter(q ->
                        studentId.equals(q.getString("student")) && questId.equals(q.getString("quest"))
                ).map(this::getQuestScoreMap).collect(Collectors.toList());
                quests.addAll(questDoc);
            }

            map.put("quests", quests);
        }
        return basicInfo;
    }

    protected Map<String, Object> getQuestScoreMap(Document q) {
        Map<String, Object> map = new HashMap<>();
        map.put("questNo", q.getString("questNo"));
        map.put("score", q.getDouble("score"));
        map.put("isObjective", q.getBoolean("isObjective"));
        return map;
    }

    //查询人员基础信息
    protected List<Map<String, Object>> packBasicInfo(String projectId, List<String> studentIds) {
        List<Map<String, Object>> studentBasicInfo = new ArrayList<>();
        for (String studentId : studentIds) {
            Map<String, Object> map = new HashMap<>();
            Document student = studentService.findStudent(projectId, studentId);
            String examNo = student.getString("examNo");
            String customExamNo = student.getString("customExamNo");
            String schoolName = schoolService.getSchoolName(projectId, student.getString("school"));
            String className = classService.getClassName(projectId, student.getString("class"));
            String name = student.getString("name");
            map.put("student", studentId);
            map.put("examNo", examNo);
            map.put("customExamNo", customExamNo);
            map.put("schoolName", schoolName);
            map.put("className", className);
            map.put("studentName", name);
            studentBasicInfo.add(map);
        }
        return studentBasicInfo;
    }

    //查询题目得分明细
    protected List<Document> packQuestDetail(String projectId, String subjectId) {
        return scoreService.getScoreAndQuestId(projectId, subjectId);
    }
}
