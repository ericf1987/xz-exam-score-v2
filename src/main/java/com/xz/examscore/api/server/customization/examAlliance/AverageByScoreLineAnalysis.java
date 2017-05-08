package com.xz.examscore.api.server.customization.examAlliance;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.ajia.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.ProvinceService;
import com.xz.examscore.services.SchoolService;
import com.xz.examscore.services.ScoreService;
import com.xz.examscore.services.SubjectService;
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
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true)
})
@Service
public class AverageByScoreLineAnalysis implements Server {

    @Autowired
    ScoreService scoreService;

    @Autowired
    SchoolService schoolService;

    @Autowired
    ProvinceService provinceService;

    @Autowired
    SubjectService subjectService;

    //分数线
    public static final double SCORE_LINE = 800;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        List<Document> projectSchools = schoolService.getProjectSchools(projectId);
        String province = provinceService.getProjectProvince(projectId);
        List<String> subjectIds = subjectService.querySubjects(projectId);
        //先统计总体数据
        Map<String, Object> provinceData = getProvinceData(projectId, Range.province(province), subjectIds, SCORE_LINE);
        List<Map<String, Object>> schoolsData = getSchoolsData(projectId, projectSchools, subjectIds, SCORE_LINE);
        return Result.success().set("subjectIds", subjectIds).set("provinceData", provinceData).set("schoolsData", schoolsData);
    }

    private Map<String, Object> getProvinceData(String projectId, Range provinceRange, List<String> subjectIds, double scoreLine) {
        Map<String, Object> provinceData = getAverageData(projectId, provinceRange, subjectIds, scoreLine);
        return provinceData;
    }

    private List<Map<String, Object>> getSchoolsData(String projectId, List<Document> projectSchools, List<String> subjectIds, double scoreLine) {
        List<Map<String, Object>> schoolsData = new ArrayList<>();
        for (Document doc : projectSchools) {
            String schoolId = doc.getString("school");
            Range schoolRange = Range.school(schoolId);
            Map<String, Object> averageData = getAverageData(projectId, schoolRange, subjectIds, scoreLine);
            averageData.put("schoolName", schoolService.getSchoolName(projectId, schoolId));
            schoolsData.add(averageData);
        }
        return schoolsData;
    }

    private Map<String, Object> getAverageData(String projectId, Range range, List<String> subjectIds, double scoreLine) {
        Map<String, Object> provinceData = new HashMap<>();

        //填充全科数据
        Target target = Target.project(projectId);
        List<Document> list = scoreService.getListByMinScore(projectId, range, target, scoreLine);
        List<String> studentIds = list.stream().map(doc -> {
            Document studentDoc = (Document) doc.get("range");
            return studentDoc.getString("id");
        }).collect(Collectors.toList());
        int count = list.size();
        double totalScore = list.stream().mapToDouble(doc -> doc.getDouble("totalScore")).sum();
        double average = DoubleUtils.round(totalScore / count);
        provinceData.put("count", count);
        provinceData.put("projectAverage", average);

        //填充各科数据
        List<Map<String, Object>> subjects = new ArrayList<>();
        for (String subjectId : subjectIds) {
            Map<String, Object> subjectMap = new HashMap<>();
            double subjectTotalScore = 0;
            for (String studentId : studentIds) {
                double score = scoreService.getScore(projectId, Range.student(studentId), Target.subject(subjectId));
                subjectTotalScore += score;
            }
            double aver = DoubleUtils.round(subjectTotalScore / count);
            subjectMap.put("subjectId", subjectId);
            subjectMap.put("totalScore", subjectTotalScore);
            subjectMap.put("count", count);
            subjectMap.put("average", aver);
            subjects.add(subjectMap);
        }

        //封装数据
        provinceData.put("subjects", subjects);
        return provinceData;
    }
}
