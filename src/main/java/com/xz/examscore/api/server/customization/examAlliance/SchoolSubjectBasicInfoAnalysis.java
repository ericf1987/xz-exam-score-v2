package com.xz.examscore.api.server.customization.examAlliance;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.*;
import com.xz.examscore.util.DoubleUtils;
import org.apache.commons.collections.MapUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author by fengye on 2016/12/25.
 */
@Function(description = "联考项目-单科各校基本情况", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "subjectId", type = Type.String, description = "考试科目ID", required = true)
})
@Service
public class SchoolSubjectBasicInfoAnalysis implements Server {

    @Autowired
    SchoolService schoolService;

    @Autowired
    MinMaxScoreService minMaxScoreService;

    @Autowired
    AverageService averageService;

    @Autowired
    ScoreRateService scoreRateService;

    @Autowired
    FullScoreService fullScoreService;

    @Autowired
    TargetService targetService;

    @Autowired
    StudentService studentService;

    @Autowired
    ProvinceService provinceService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String subjectId = param.getString("subjectId");
        List<Map<String, Object>> result = new ArrayList<>();
        //所有学校
        List<Document> projectSchools = schoolService.getProjectSchools(projectId);
        //统计各科数据
        projectSchools.forEach(projectSchool -> handleData(projectId, subjectId, result, projectSchool));

        Collections.sort(result, (Map<String, Object> m1, Map<String, Object> m2) -> {
            Double s1 = MapUtils.getDouble(m1, "average");
            Double s2 = MapUtils.getDouble(m2, "average");
            return s2.compareTo(s1);
        });

        AtomicInteger rank = new AtomicInteger();
        result.forEach(projectSchool -> projectSchool.put("rank", rank.incrementAndGet()));
        Map<String, Object> provinceMap = handleProcessData(projectId, subjectId);
        provinceMap.put("rank", 1);

        return Result.success().set("schoolSubjectBasicInfo", result).set("provinceSubjectBasicInfo", provinceMap);
    }

    private Map<String, Object> handleProcessData(String projectId, String subjectId) {
        Range provinceRange = Range.province(provinceService.getProjectProvince(projectId));
        Map<String, Object> map = getRangeMap(projectId, subjectId, "", provinceRange);
        return map;
    }

    public void handleData(String projectId, String subjectId, List<Map<String, Object>> result, Document projectSchool) {
        String schoolId = projectSchool.getString("school");
        String schoolName = schoolService.getSchoolName(projectId, schoolId);
        Range schoolRange = Range.school(schoolId);
        Map<String, Object> schoolMap = getRangeMap(projectId, subjectId, schoolName, schoolRange);
        result.add(schoolMap);
    }

    public Map<String, Object> getRangeMap(String projectId, String subjectId, String schoolName, Range schoolRange) {
        Target target = targetService.getTarget(projectId, subjectId);
        //参考人数
        int studentCount = studentService.getStudentCount(projectId, schoolRange, target);
        //最高分
        double[] minMaxScore = minMaxScoreService.getMinMaxScore(projectId, schoolRange, target);
        double max = minMaxScore[1];
        //平均分
        double average = averageService.getAverage(projectId, schoolRange, target);
        //得分率
        double fullScore = fullScoreService.getFullScore(projectId, target);
        double scoreRate = DoubleUtils.round(average / fullScore, true);
        Map<String, Object> schoolMap = new HashMap<>();
        schoolMap.put("schoolName", StringUtil.isBlank(schoolName) ? "总体" : schoolName);
        schoolMap.put("max", max);
        schoolMap.put("average", average);
        schoolMap.put("scoreRate", scoreRate);
        schoolMap.put("studentCount", studentCount);
        return schoolMap;
    }
}
