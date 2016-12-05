package com.xz.examscore.api.server.customization.examAlliance;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
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
 * @author by fengye on 2016/12/5.
 */
@Function(description = "联考项目-统计全科分数高于指定分数线的学生的各科平均分", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "rankSegment", type = Type.String, description = "考试项目ID", required = true)
})
@Service
public class AverageByRankLineAnalysis implements Server {

    @Autowired
    StudentService studentService;

    @Autowired
    ScoreService scoreService;

    @Autowired
    RankService rankService;

    @Autowired
    SchoolService schoolService;

    @Autowired
    ProvinceService provinceService;

    @Autowired
    SubjectService subjectService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String rankSegment = param.getString("rankSegment");
        String province = provinceService.getProjectProvince(projectId);
        Range provinceRange = Range.province(province);
        List<Document> projectSchools = schoolService.getProjectSchools(projectId);
        List<String> subjectIds = subjectService.querySubjects(projectId);
        Map<String, Object> provinceData = getProvinceData(projectId, provinceRange, subjectIds, rankSegment);
        List<Map<String, Object>> schoolsData = getSchoolData(projectId, projectSchools, subjectIds, rankSegment);
        return Result.success().set("subjectIds", subjectIds).set("provinceData", provinceData).set("schoolsData", schoolsData);
    }

    private Map<String, Object> getProvinceData(String projectId, Range provinceRange, List<String> subjectIds, String rankSegment) {
        Map<String, Object> provinceMap = new HashMap<>();
        //统计人数
        int studentCount = studentService.getStudentCount(projectId, provinceRange, Target.project(projectId));
        //得出占比人数
        int requiredCount = (int) (studentCount * Double.parseDouble(rankSegment));
        //占比
        double rate = DoubleUtils.round((double) requiredCount / studentCount);
        //查出占比人数中最后一名对应的分数
        double rankScore = rankService.getRankScore(projectId, provinceRange, Target.project(projectId), requiredCount);

        List<Document> studentListByScore = scoreService.getListByScore(projectId, provinceRange, Target.project(projectId), rankScore);

        List<Map<String, Object>> averageData = getAverageData(projectId, provinceRange, subjectIds, studentListByScore, requiredCount);

        provinceMap.put("count", requiredCount);
        provinceMap.put("rate", rate);
        provinceMap.put("averageData", averageData);
        return provinceMap;
    }

    private List<Map<String, Object>> getSchoolData(String projectId, List<Document> projectSchools, List<String> subjectIds, String rankSegment) {
        List<Map<String, Object>> schoolsData = new ArrayList<>();
        for (Document schoolDoc : projectSchools) {
            Map<String, Object> schoolMap = new HashMap<>();
            Range schoolRange = Range.school(schoolDoc.getString("school"));
            //统计人数
            int studentCount = studentService.getStudentCount(projectId, Range.school(schoolDoc.getString("school")), Target.project(projectId));
            //得出占比人数
            int requiredCount = (int) (studentCount * Double.parseDouble(rankSegment));
            //占比
            double rate = DoubleUtils.round((double) requiredCount / studentCount);
            //查出占比人数中最后一名对应的分数
            double rankScore = rankService.getRankScore(projectId, schoolRange, Target.project(projectId), requiredCount);
            List<Document> studentListByScore = scoreService.getListByScore(projectId, schoolRange, Target.project(projectId), rankScore);

            List<Map<String, Object>> averageData = getAverageData(projectId, schoolRange, subjectIds, studentListByScore, requiredCount);
            schoolMap.put("schoolName", schoolService.getSchoolName(projectId, schoolRange.getId()));
            schoolMap.put("count", requiredCount);
            schoolMap.put("rate", rate);
            schoolMap.put("averageData", averageData);
            schoolsData.add(schoolMap);
        }
        return schoolsData;
    }

    private List<Map<String, Object>> getAverageData(String projectId, Range range, List<String> subjectIds, List<Document> studentListByScore, int requiredCount) {
        List<Map<String, Object>> averageData = new ArrayList<>();
        Map<String, Object> projectAverage = new HashMap<>();
        //统计全科数据
        List<String> studentIds = studentListByScore.stream().map(doc -> {
            Document student = (Document) doc.get("range");
            return student.getString("id");
        }).collect(Collectors.toList());
        double totalScore = studentListByScore.stream().mapToDouble(l -> l.getDouble("totalScore")).sum();
        double average = DoubleUtils.round(totalScore / requiredCount);
        projectAverage.put("subjectName", "全科");
        projectAverage.put("subjectId", projectId);
        projectAverage.put("totalScore", totalScore);
        projectAverage.put("average", average);
        averageData.add(projectAverage);

        //统计各科数据
        for (String subjectId : subjectIds) {
            Map<String, Object> subjectAverage = new HashMap<>();
            int subjectTotalScore = 0;
            for (String studentId : studentIds) {
                double score = scoreService.getScore(projectId, Range.student(studentId), Target.subject(subjectId));
                subjectTotalScore += score;
            }
            double aver = DoubleUtils.round(subjectTotalScore / requiredCount);
            subjectAverage.put("subjectName", SubjectService.getSubjectName(subjectId));
            subjectAverage.put("subjectId", subjectId);
            subjectAverage.put("totalScore", subjectTotalScore);
            subjectAverage.put("average", aver);
            averageData.add(subjectAverage);
        }
        return averageData;
    }
}
