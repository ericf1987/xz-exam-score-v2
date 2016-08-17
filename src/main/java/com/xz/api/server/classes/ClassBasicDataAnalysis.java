package com.xz.api.server.classes;

import com.xz.ajiaedu.common.lang.Result;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author by fengye on 2016/6/24.
 */
@SuppressWarnings("unchecked")
@Function(description = "班级成绩-学生各科成绩明细", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "schoolId", type = Type.String, description = "学校id", required = true),
        @Parameter(name = "classId", type = Type.String, description = "班级id", required = true)
})
@Service
public class ClassBasicDataAnalysis implements Server {
    public static Logger LOG = LoggerFactory.getLogger(ClassBasicDataAnalysis.class);

    @Autowired
    RankSegmentService rankSegmentService;

    @Autowired
    StudentService studentService;

    @Autowired
    RankService rankService;

    @Autowired
    ScoreService scoreService;

    @Autowired
    SchoolService schoolService;

    @Autowired
    ClassService classService;

    @Autowired
    SubjectService subjectService;

    @Autowired
    CityService cityService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String schoolId = param.getString("schoolId");
        String classId = param.getString("classId");

        //学校名称
        String schoolName = schoolService.getSchoolName(projectId, schoolId);
        String className = classService.getClassName(projectId, classId);
        List<Map<String, Object>> studentBasicData = new ArrayList<>();

        List<Document> studentList = studentService.getStudentList(projectId, Range.clazz(classId));


        for(Document student : studentList){
            Map<String, Object> map = new HashMap<>();
            String studentId = student.getString("student");
            String cityName = cityService.getCityName(student.getString("city"));
            map.put("examNo", student.getString("examNo"));
            map.put("studentName", student.getString("name"));
            map.put("school", schoolName);
            map.put("class", className);
            map.put("city", cityName);

            //获取省排名
            Range province = Range.province(student.getString("province"));
            Map<String, Object> projectAnalysis = getProjectAnalysis(projectId, Range.clazz(classId), Range.school(schoolId), province, studentId);
            List<Map<String, Object>> subjectAnalysis = getSubjectAnalysis(projectId, Range.clazz(classId), Range.school(schoolId), province, studentId);
            map.put("projectAnalysis", projectAnalysis);
            map.put("subjectAnalysis", subjectAnalysis);
            studentBasicData.add(map);
        }

        //排序
        studentBasicData.sort((o1, o2) -> {
            Double score1 = (Double) ((Map<String, Object>) o1.get("projectAnalysis")).get("score");
            Double score2 = (Double) ((Map<String, Object>) o2.get("projectAnalysis")).get("score");
            return score2.compareTo(score1);
        });
        return Result.success().set("studentBasicData", studentBasicData);
    }

    private Map<String,Object> getProjectAnalysis(String projectId, Range clazz, Range school, Range province, String studentId) {
        Map<String, Object> map = new HashMap<>();
        //获取总分(全科目总分)
        double score = scoreService.getScore(projectId, Range.student(studentId), Target.project(projectId));
        //获取排名
        int classRankIndex = rankService.getRank(projectId, clazz, Target.project(projectId), studentId);
        int schoolRankIndex = rankService.getRank(projectId, school, Target.project(projectId), studentId);
        int totalRankIndex = rankService.getRank(projectId, province, Target.project(projectId), studentId);
        map.put("score", score);
        map.put("classRankIndex", classRankIndex);
        map.put("schoolRankIndex", schoolRankIndex);
        map.put("totalRankIndex", totalRankIndex);
        return map;
    }

    private List<Map<String,Object>> getSubjectAnalysis(String projectId, Range clazz, Range school, Range province, String studentId) {
        //获取考试的科目
        List<String> subjects = subjectService.querySubjects(projectId);
        List<Map<String, Object>> subjectAnalysis = new ArrayList<>();
        for(String subject : subjects){
            Map<String, Object> map = new HashMap<>();
            map.put("subjectId", subject);
            map.put("subjectName", SubjectService.getSubjectName(subject));
            //获取科目总分(具体科目分数)
            double score = scoreService.getScore(projectId, Range.student(studentId), Target.subject(subject));
            //获取排名
            int clazzRankIndex = rankService.getRank(projectId, clazz, Target.subject(subject), studentId);
            int schoolRankIndex = rankService.getRank(projectId, school, Target.subject(subject), studentId);
            int totalRankIndex = rankService.getRank(projectId, province, Target.subject(subject), studentId);
            map.put("score", score);
            map.put("classRankIndex", clazzRankIndex);
            map.put("schoolRankIndex", schoolRankIndex);
            map.put("totalRankIndex", totalRankIndex);
            subjectAnalysis.add(map);
        }
        return subjectAnalysis;
    }
}
