package com.xz.examscore.api.server.project;

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
@Function(description = "总体成绩-学生各科成绩明细分析", parameters = {
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
                String cityName = cityService.getCityName(student.getString("city"));
                String studentName = student.getString("name");
                String studentId = student.getString("student");
                String className = classService.getClassName(projectId, student.getString("class"));
                map.put("studentName", studentName);
                map.put("examNo", student.getString("examNo"));
                map.put("customExamNo", student.getString("customExamNo"));
                map.put("class", className);
                map.put("school", schoolName);
                map.put("city", cityName);

                //获取省排名
                Range province = Range.province(student.getString("province"));
                Map<String, Object> projectAnalysis = getProjectAnalysis(projectId, province, studentId);
                List<Map<String, Object>> subjectAnalysis = getSubjectAnalysis(projectId, province, studentId);
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

    private Map<String,Object> getProjectAnalysis(String projectId, Range province, String studentId) {
        Map<String, Object> map = new HashMap<>();
        //获取总分(全科目总分)
        double score = scoreService.getScore(projectId, Range.student(studentId), Target.project(projectId));
        //获取排名
        int totalRankIndex = rankService.getRank(projectId, province, Target.project(projectId), studentId);
        map.put("score", score);
        map.put("totalRankIndex", totalRankIndex);
        return map;
    }

    private List<Map<String,Object>> getSubjectAnalysis(String projectId, Range province, String studentId) {

        // 获取考试的科目
        List<String> subjects = subjectService.querySubjects(projectId);
        List<Map<String, Object>> subjectAnalysis = new ArrayList<>();

        for(String subject : subjects){
            Map<String, Object> map = new HashMap<>();
            map.put("subjectId", subject);
            map.put("subjectName", SubjectService.getSubjectName(subject));
            //获取科目总分(具体科目分数)
            double score = scoreService.getScore(projectId, Range.student(studentId), Target.subject(subject));
            //获取排名
            int totalRankIndex = rankService.getRank(projectId, province, Target.subject(subject), studentId);
            map.put("score", score);
            map.put("totalRankIndex", totalRankIndex);
            subjectAnalysis.add(map);
        }
        return subjectAnalysis;
    }
}
