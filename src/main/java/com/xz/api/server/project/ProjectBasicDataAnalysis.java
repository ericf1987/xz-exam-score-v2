package com.xz.api.server.project;

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
@Function(description = "总体成绩-基础数据分析", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "schoolIds", type = Type.StringArray, description = "学校id列表", required = true)
})
@Service
public class ProjectBasicDataAnalysis implements Server {

    public static Logger LOG = LoggerFactory.getLogger(ProjectBasicDataAnalysis.class);

    @Autowired
    RankSegmentService rankSegmentService;

    @Autowired
    SchoolService schoolService;

    @Autowired
    StudentService studentService;

    @Autowired
    RankService rankService;

    @Autowired
    ScoreService scoreService;

    @Autowired
    ClassService classService;

    @Autowired
    SubjectService subjectService;

    @Autowired
    CityService cityService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String[] schoolIds = param.getStringValues("schoolIds");

        List<Map<String, Object>> studentBasicData = new ArrayList<>();

        for(String schoolId : schoolIds){
            //获取基础信息
            String schoolName = schoolService.getSchoolName(projectId, schoolId);
            List<Document> studentList = studentService.getStudentList(projectId, Range.school(schoolId));
            for(Document student : studentList){
                Map<String, Object> map = new HashMap<>();
                //获取学生学校名称
                Range city = Range.city(student.getString("city"));
                String cityName = cityService.getCityName(student.getString("city"));
                String studentName = student.getString("name");
                String studentId = student.getString("student");
                String className = classService.getClassName(projectId, student.getString("class"));
                map.put("studentName", studentName);
                map.put("class", className);
                map.put("school", schoolName);
                map.put("city", cityName);

                Map<String, Object> projectAnalysis = getProjectAnalysis(projectId, city, studentId);
                List<Map<String, Object>> subjectAnalysis = getSubjectAnalysis(projectId, city, studentId);
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

        return Result.success().set("studentBasicData", studentBasicData);
    }

    private Map<String,Object> getProjectAnalysis(String projectId, Range city, String studentId) {
        Map<String, Object> map = new HashMap<>();
        //获取总分(全科目总分)
        double score = scoreService.getScore(projectId, Range.student(studentId), Target.project(projectId));
        //获取排名(市级排名)
        int totalRankIndex = rankService.getRank(projectId, city, Target.project(projectId), studentId);
        map.put("score", score);
        map.put("totalRankIndex", totalRankIndex);
        return map;
    }

    private List<Map<String,Object>> getSubjectAnalysis(String projectId, Range city, String studentId) {
        //获取考试的科目
        List<String> subjects = subjectService.querySubjects(projectId);
        List<Map<String, Object>> subjectAnalysis = new ArrayList<>();
        for(String subject : subjects){
            Map<String, Object> map = new HashMap<>();
            map.put("subjectId", subject);
            map.put("subjectName", SubjectService.getSubjectName(subject));
            //获取科目总分(具体科目分数)
            double score = scoreService.getScore(projectId, Range.student(studentId), Target.subject(subject));
            //获取排名(科目市级排名)
            int totalRankIndex = rankService.getRank(projectId, city, Target.subject(subject), studentId);
            map.put("score", score);
            map.put("totalRankIndex", totalRankIndex);
            subjectAnalysis.add(map);
        }
        return subjectAnalysis;
    }
}
