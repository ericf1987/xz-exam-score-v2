package com.xz.examscore.asynccomponents.report.biz.classes;

import com.xz.ajiaedu.common.beans.dic.QuestType;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.*;
import com.xz.examscore.util.DoubleUtils;
import org.apache.commons.collections.MapUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author by fengye on 2017/2/10.
 */
@Service
public class ClassQuestTypeBiz implements Server {

    @Autowired
    ScoreService scoreService;

    @Autowired
    StudentService studentService;

    @Autowired
    FullScoreService fullScoreService;

    @Autowired
    TargetService targetService;

    @Autowired
    QuestTypeService questTypeService;

    @Autowired
    QuestTypeScoreService questTypeScoreService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("project");
        String classId = param.getString("classId");
        String subjectId = param.getString("subjectId");

        //当前科目的题型列表
        List<QuestType> questTypes = questTypeService.getQuestTypeList(projectId, subjectId);

        //将每个科目题型列表的元素添加满分字段
        List<Map<String, Object>> questTypeMaps = packQuestTypeItem(projectId, questTypes);

        //首先查询班级指定科目的所有题型得分信息
        List<Document> classQuestTypeScoreList = questTypeScoreService.getNonStudentQuestTypeScoreList(projectId, Range.clazz(classId));

        //查询改班级下所有学生指定科目的所有题型得分
        List<Document> studentQuestTypeScoreList = questTypeScoreService.getStudentQuestTypeScoreList(projectId, Range.clazz(classId));

        //匹配该科目的题型
        List<Document> classData = classQuestTypeScoreList.stream().filter(p -> questTypeFilter(questTypes, p)).collect(Collectors.toList());

        List<Document> studentData = studentQuestTypeScoreList.stream().filter(p -> questTypeFilter(questTypes, p)).collect(Collectors.toList());

        return Result.success().set("classes", packClassData(projectId, classData))
                .set("students", packStudentData(projectId, subjectId, classId, studentData, questTypeMaps));
    }

    private List<Map<String, Object>> packClassData(String projectId, List<Document> classData) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Document doc : classData) {
            String questTypeId = doc.getString("questType");
            Target questTypeTarget = Target.questType(questTypeId);
            double fullScore = fullScoreService.getFullScore(projectId, questTypeTarget);
            double score = doc.getDouble("average");
            double rate = doc.getDouble("rate");
            String questTypeName = questTypeService.getQuestType(projectId, questTypeId).getName();
            Map<String, Object> map = new HashMap<>();
            map.put("fullScore", fullScore);
            map.put("score", DoubleUtils.round(score, false));
            map.put("scoreRate", DoubleUtils.round(fullScore == 0 ? 0 : rate, true));
            map.put("questTypeId", questTypeId);
            map.put("name", questTypeName);
            result.add(map);
        }
        return result;
    }

    private List<Map<String, Object>> packQuestTypeItem(String projectId, List<QuestType> questTypeList) {
        List<Map<String, Object>> result = new ArrayList<>();
        questTypeList.forEach(questType -> {
            Map<String, Object> m = new HashMap<>();
            m.put("questTypeId", questType.getId());
            m.put("name", questType.getName());
            m.put("fullScore", fullScoreService.getFullScore(projectId, Target.questType(questType.getId())));
            result.add(m);
        });
        return result;
    }

    private List<Map<String, Object>> packStudentData(String projectId, String subjectId, String classId, List<Document> studentData, List<Map<String, Object>> questTypeList) {
        List<Map<String, Object>> result = new ArrayList<>();
        List<String> studentIds = studentService.getStudentIds(projectId, subjectId, Range.clazz(classId));
        for (String studentId : studentIds){
            Document student = studentService.findStudent(projectId, studentId);
            Map<String, Object> map = new HashMap<>();

            //学生基础信息
            String studentName = student.getString("name");
            map.put("examNo", student.getString("examNo"));
            map.put("customExamNo", student.getString("customExamNo"));
            map.put("studentId", studentId);
            map.put("studentName", studentName);

            //学生每个试卷题型的得分信息
            List<Map<String, Object>> questTypes = packScoreAndRate(studentId, studentData, questTypeList);
            map.put("questTypes", questTypes);
            result.add(map);
        }

        return result;
    }

    private List<Map<String, Object>> packScoreAndRate(String studentId, List<Document> studentData, List<Map<String, Object>> questTypeList) {
        for (Map<String, Object> questTypeMap : questTypeList){
            String questTypeId = MapUtils.getString(questTypeMap, "questTypeId");
            double score = 0;
            double rate = 0;
            for(Document doc : studentData){
                if(doc.getString("student").equals(studentId) && doc.getString("questType").equals(questTypeId)){
                    score = doc.getDouble("score");
                    rate = doc.getDouble("rate");
                    break;
                }
            }
            questTypeMap.put("score", score);
            questTypeMap.put("rate", rate);
        }
        return questTypeList;
    }

    public boolean questTypeFilter(List<QuestType> questTypeList, Document p) {
        String questType = p.getString("questType");
        for (QuestType qt : questTypeList) {
            if (questType.equals(qt.getId())) {
                return true;
            }
        }
        return false;
    }
}
