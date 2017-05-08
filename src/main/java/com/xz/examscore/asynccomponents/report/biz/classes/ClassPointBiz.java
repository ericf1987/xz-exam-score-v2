package com.xz.examscore.asynccomponents.report.biz.classes;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.ajia.Param;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.bean.Point;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.*;
import com.xz.examscore.util.DoubleUtils;
import org.apache.commons.collections.MapUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author by fengye on 2017/2/9.
 */
@Service
public class ClassPointBiz implements Server {

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
        String projectId = param.getString("projectId");
        String subjectId = param.getString("subjectId");
        String classId = param.getString("classId");

        //获取当前科目的所有知识点
        List<String> pointIds = pointService.getPoints(projectId, subjectId).stream().map(Point::getId).collect(Collectors.toList());

        Collections.sort(pointIds);

        //查询当前班级当前科目下所有知识点的平均分集合
        ArrayList<Document> pointByClazz = averageService.getAverageByTargetIds(projectId, Range.clazz(classId), pointIds);
        //查询出所有学生知识点
//        ArrayList<Document> pointByStudent = scoreService.getTotalScoreByTargetIds(projectId, Range.STUDENT, pointIds);

        List<String> studentIds = studentService.getStudentIds(projectId, subjectId, Range.clazz(classId));
        List<Document> pointByStudentList = new ArrayList<>();
        for (String studentId : studentIds) {
            //找出当前学生和指定科目的数据
            ArrayList<Document> totalScoreByTargetIds = scoreService.getTotalScoreByTargetIds(projectId, Range.student(studentId), pointIds);
            pointByStudentList.addAll(totalScoreByTargetIds);
        }

        List<Map<String, Object>> points = pointByClazz.stream().map(p -> {
            Document doc = (Document) p.get("target");
            String pointId = doc.getString("id");
            String pointName = pointService.getPointName(pointId);
            double fullScore = fullScoreService.getFullScore(projectId, Target.point(pointId));
            Map<String, Object> map = new HashMap<>();
            map.put("pointId", pointId);
            map.put("pointName", pointName);
            map.put("fullScore", fullScore);
            return map;
        }).collect(Collectors.toList());

        return Result.success().set("classes", packClazzData(projectId, pointByClazz))
                .set("students", packStudentData(projectId, pointByStudentList, studentIds, points));
    }

    private List<Map<String, Object>> packClazzData(String projectId, List<Document> pointByClazz) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Document doc : pointByClazz) {
            Document targetDoc = (Document) doc.get("target");
            String pointId = targetDoc.getString("id");
            String pointName = pointService.getPointName(pointId);
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

    private List<Map<String, Object>> packStudentData(String projectId, List<Document> pointByStudentList, List<String> studentIds, List<Map<String, Object>> points) {
        List<Map<String, Object>> result = new ArrayList<>();

        for (String studentId : studentIds) {
            Map<String, Object> map = new HashMap<>();

            //学生基础信息
            Document student = studentService.findStudent(projectId, studentId);
            String studentName = student.getString("name");
            map.put("studentId", studentId);
            map.put("studentName", studentName);
            map.put("examNo", student.getString("examNo"));
            map.put("customExamNo", student.getString("customExamNo"));

            List<Map<String, Object>> pointStats = new ArrayList<>();
            for (Map<String, Object> pointMap : points) {
                String pointId = MapUtils.getString(pointMap, "pointId");
                String pointName = MapUtils.getString(pointMap, "pointName");
                //获取该学生指定知识点的得分
                double score = getScoreByKey(studentId, pointByStudentList, pointId);
                double fullScore = MapUtils.getDouble(pointMap, "fullScore");
                double scoreRate = DoubleUtils.round(fullScore == 0 ? 0 : score / fullScore, true);
                Map<String, Object> m = new HashMap<>();
                m.put("pointId", pointId);
                m.put("pointName", pointName);
                m.put("score", score);
                m.put("fullScore", fullScore);
                m.put("scoreRate", scoreRate);
                pointStats.add(m);
            }
            map.put("pointStats", pointStats);
            result.add(map);
        }
        return result;
    }

    private double getScoreByKey(String studentId, List<Document> pointByStudentList, String key) {
        for (Document doc : pointByStudentList) {
            Document rangeDoc = (Document) doc.get("range");
            Document targetDoc = (Document) doc.get("target");
            if (studentId.equals(rangeDoc.getString("id")) && key.equals(targetDoc.getString("id"))) {
                return doc.getDouble("totalScore");
            }
        }
        return 0;
    }
}
