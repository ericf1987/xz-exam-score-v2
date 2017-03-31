package com.xz.examscore.api.server.school;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.*;
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
@Function(description = "学校成绩-学生各科成绩明细分析", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "schoolId", type = Type.String, description = "学校id", required = true)
})
@Service
public class SchoolBasicDataAnalysis implements Server {
    public static Logger LOG = LoggerFactory.getLogger(SchoolBasicDataAnalysis.class);

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

        //全科缺考学生列表
        List<String> projectAbsentStudents = new ArrayList<>();

        List<Map<String, Object>> studentBasicData = new ArrayList<>();
        String schoolName = schoolService.getSchoolName(projectId, schoolId);
        //获取学校各个班级学生
        List<Document> classList = classService.listClasses(projectId, schoolId);
        for(Document clazz : classList){
            String className = classService.getClassName(projectId, clazz.getString("class"));
            List<Document> studentList = studentService.getStudentList(projectId, Range.clazz(clazz.getString("class")));
            for(Document student : studentList){
                String studentId = student.getString("student");
                String cityName = cityService.getCityName(student.getString("city"));
                Map<String, Object> map = new HashMap<>();
                map.put("studentId", studentId);
                map.put("studentName", student.getString("name"));
                map.put("examNo", student.getString("examNo"));
                map.put("customExamNo", student.getString("customExamNo"));
                map.put("class", className);
                map.put("school", schoolName);
                map.put("city", cityName);

                //获取省排名
                Range province = Range.province(student.getString("province"));
                Map<String, Object> projectAnalysis = getProjectAnalysis(projectId, Range.school(schoolId), province, studentId, projectAbsentStudents);
                List<Map<String, Object>> subjectAnalysis = getSubjectAnalysis(projectId, Range.school(schoolId), province, studentId);

                map.put("projectAnalysis", projectAnalysis);
                map.put("subjectAnalysis", subjectAnalysis);
                studentBasicData.add(map);
            }
        }
        //排序
        studentBasicData.sort((o1, o2) -> {
            Double score1 = (Double) ((Map<String, Object>) o1.get("projectAnalysis")).get("score");
            Double score2 = (Double) ((Map<String, Object>) o2.get("projectAnalysis")).get("score");
            return score2.compareTo(score1);
        });

        return Result.success().set("studentBasicData", studentBasicData).set("projectAbsentStudents", projectAbsentStudents);
    }

    private Map<String,Object> getProjectAnalysis(String projectId, Range school, Range province, String studentId, List<String> projectAbsentStudents) {
        Map<String, Object> map = new HashMap<>();
        //获取总分(全科目总分)
        double score = scoreService.getScore(projectId, Range.student(studentId), Target.project(projectId));

        boolean isAbsent = scoreService.isAbsentInTotalScore(projectId, studentId, Target.project(projectId));

        if(isAbsent){
            projectAbsentStudents.add(studentId);
        }

        //获取排名
        int schoolRankIndex = rankService.getRank(projectId, school, Target.project(projectId), studentId);
        int totalRankIndex = rankService.getRank(projectId, province, Target.project(projectId), studentId);
        map.put("score", score);
        map.put("schoolRankIndex", schoolRankIndex);
        map.put("totalRankIndex", totalRankIndex);
        return map;
    }

    private List<Map<String,Object>> getSubjectAnalysis(String projectId, Range school, Range province, String studentId) {
        //获取考试的科目
        List<String> subjects = subjectService.querySubjects(projectId);
        List<Map<String, Object>> subjectAnalysis = new ArrayList<>();
        for(String subject : subjects){
            Map<String, Object> map = new HashMap<>();
            map.put("subjectId", subject);
            map.put("subjectName", SubjectService.getSubjectName(subject));
            //获取科目总分(具体科目分数)
            String score = scoreService.getAbsentTotalScore(projectId, Range.student(studentId), Target.subject(subject));
            //获取排名
            int schoolRankIndex = rankService.getRank(projectId, school, Target.subject(subject), studentId);
            int totalRankIndex = rankService.getRank(projectId, province, Target.subject(subject), studentId);
            map.put("score", score);
            map.put("schoolRankIndex", schoolRankIndex);
            map.put("totalRankIndex", totalRankIndex);
            subjectAnalysis.add(map);
        }
        return subjectAnalysis;
    }
}
