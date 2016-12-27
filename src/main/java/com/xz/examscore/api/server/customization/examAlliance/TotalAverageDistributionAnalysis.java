package com.xz.examscore.api.server.customization.examAlliance;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.bean.Range;
import com.xz.examscore.services.*;
import com.xz.examscore.util.DoubleUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author by fengye on 2016/12/26.
 */
@Function(description = "联考项目-前百分段名平均分", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true)
})
@Service
public class TotalAverageDistributionAnalysis implements Server {

    @Autowired
    ProvinceService provinceService;

    @Autowired
    AverageService averageService;

    @Autowired
    SchoolService schoolService;

    @Autowired
    SubjectService subjectService;

    @Autowired
    TargetService targetService;

    @Autowired
    SubjectCombinationService subjectCombinationService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String province = provinceService.getProjectProvince(projectId);
        Range provinceRange = Range.province(province);
        List<String> subjectIds = subjectService.querySubjects(projectId);
        List<String> combinedSubjectIds = subjectCombinationService.getAllSubjectCombinations(projectId);
        List<Document> projectSchools = schoolService.getProjectSchools(projectId);
        Map<String, Object> resultMap = new HashMap<>();

        List<Map<String, Object>> totalData = getRangeAverageDistribution(projectId, provinceRange, subjectIds, combinedSubjectIds);
        resultMap.put("totalData", totalData);

        List<Map<String, Object>> schoolData = new ArrayList<>();
        projectSchools.forEach(projectSchool -> {
            Map<String, Object> schoolMap = new HashMap<>();
            String schoolId = projectSchool.getString("school");
            schoolMap.put("schoolId", schoolId);
            schoolMap.put("schoolName", schoolService.getSchoolName(projectId, schoolId));
            Range range = Range.school(projectSchool.getString("school"));
            schoolMap.put("subjects", getRangeAverageDistribution(projectId, range, subjectIds, combinedSubjectIds));
            schoolData.add(schoolMap);
        });
        resultMap.put("schoolData", schoolData);

        return Result.success().set("totalAverageDistribution", resultMap);
    }

    private List<Map<String, Object>> getRangeAverageDistribution(String projectId, Range range, List<String> subjectIds, List<String> combinedSubjectIds) {
        List<Map<String, Object>> result = new ArrayList<>();
        result.add(getAverageMap(projectId, range, ""));
        subjectIds.forEach(subjectId -> {
            Map<String, Object> map = getAverageMap(projectId, range, subjectId);
            result.add(map);
        });
        combinedSubjectIds.forEach(combinedSubjectId -> {
            Map<String, Object> map = getAverageMap(projectId, range, combinedSubjectId);
            result.add(map);
        });
        return result;
    }

    private Map<String, Object> getAverageMap(String projectId, Range range, String subjectId) {
        String subjectName = SubjectService.getSubjectName(subjectId);
        double average = averageService.getAverage(projectId, range, targetService.getTarget(projectId, subjectId));
        Map<String, Object> map = new HashMap<>();
        map.put("subjectId", subjectId);
        map.put("subjectName", subjectName);
        map.put("average", DoubleUtils.round(average));
        return map;
    }
}
