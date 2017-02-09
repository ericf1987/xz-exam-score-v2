package com.xz.examscore.asynccomponents.report.biz.classes;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.*;
import com.xz.examscore.util.DoubleUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author by fengye on 2017/2/9.
 */
@Service
public class ClassPointBiz implements Server{

    @Autowired
    ScoreService scoreService;

    @Autowired
    AverageService averageService;

    @Autowired
    PointService pointService;

    @Autowired
    TargetService targetService;

    @Autowired
    StudentService studentService;

    @Autowired
    FullScoreService fullScoreService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("project");
        String subjectId = param.getString("subjectId");
        String classId = param.getString("classId");
        //查询出所有班级知识点
        ArrayList<Document> pointByClazz = averageService.getAverageByName(projectId, Range.CLASS, Target.POINT);

        //查询出所有学生知识点
        ArrayList<Document> pointByStudent = scoreService.getTotalScoreByName(projectId, Range.STUDENT, Target.POINT);

        //找出当前班级和指定科目的数据
        List<Document> pointByClazzList = pointByClazz.stream().filter(p -> {
            Document rangeDoc = (Document)p.get("range");
            return rangeDoc.getString("id").equals(classId);
        }).filter(p -> gg(projectId, subjectId, p)).collect(Collectors.toList());


        List<Document> studentList = studentService.getStudentList(projectId, Range.clazz(classId));
        List<Document> pointByStudentList = new ArrayList<>();
        for (Document studentDoc : studentList){
            String studentId = studentDoc.getString("student");
            //找出当前学生和指定科目的数据
            List<Document> one = pointByStudent.stream().filter(p -> {
                Document rangeDoc = (Document)p.get("range");
                return rangeDoc.getString("id").equals(studentId);
            }).filter(p -> gg(projectId, subjectId, p)).collect(Collectors.toList());
            pointByStudentList.addAll(one);
        }

        Map<String, Object> map = new HashMap<>();

        return Result.success().set("classes", packClazzData(projectId, pointByClazzList, map))
                .set("students", packStudentData(projectId, pointByStudentList, map));
    }

    public boolean gg(String projectId, String subjectId, Document p) {
        Document targetDoc = (Document)p.get("target");
        return targetService.getTargetSubjectId(projectId, Target.point(targetDoc.getString("id"))).equals(subjectId);
    }

    private List<Map<String, Object>> packClazzData(String projectId, List<Document> pointByClazz, Map<String, Object> map) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Document doc : pointByClazz){
            Document targetDoc = (Document) doc.get("target");
            String pointId = targetDoc.getString("id");
            String pointName = pointService.getPointName(pointId);
            map.put(pointId, pointName);
            double average = doc.getDouble("average");
            Target pointTarget = Target.point(pointId);
            Map<String, Object> pointMap = new HashMap<>();
            double fullScore = fullScoreService.getFullScore(projectId, pointTarget);
            pointMap.put("pointName", pointName);
            pointMap.put("score", DoubleUtils.round(average));
            pointMap.put("fullScore", fullScore);
            pointMap.put("scoreRate", DoubleUtils.round(fullScore == 0 ? 0 : average / fullScore, true));
            result.add(pointMap);
        }
        return result;
    }

    private List<Map<String, Object>> packStudentData(String projectId, List<Document> pointByStudentList, Map<String, Object> map) {
        List<Map<String, Object>> result = new ArrayList<>();
        for(Document doc : pointByStudentList){
            Document targetDoc = (Document) doc.get("target");
            String pointId = targetDoc.getString("id");
            String pointName = map.get(pointId).toString();
            double score = doc.getDouble("totalScore");
            Target pointTarget = Target.point(pointId);
            Map<String, Object> pointMap = new HashMap<>();
            double fullScore = fullScoreService.getFullScore(projectId, pointTarget);
            pointMap.put("pointName", pointName);
            pointMap.put("score", DoubleUtils.round(score));
            pointMap.put("fullScore", fullScore);
            pointMap.put("scoreRate", DoubleUtils.round(fullScore == 0 ? 0 : score / fullScore, true));
            result.add(pointMap);
        }
        return result;
    }
}
