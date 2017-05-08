package com.xz.examscore.api.server.customization.examAlliance;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.ajia.Param;
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
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author by fengye on 2016/12/30.
 */
@Function(description = "联考项目-前百分段名平均分", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true)
})
@Service
public class SubjectTopAverageAnalysis implements Server {

    @Autowired
    SubjectService subjectService;

    @Autowired
    SubjectCombinationService subjectCombinationService;

    @Autowired
    ProvinceService provinceService;

    @Autowired
    TargetService targetService;

    @Autowired
    SchoolService schoolService;

    @Autowired
    ScoreService scoreService;

    @Autowired
    AverageService averageService;

    @Autowired
    MinMaxScoreService minMaxScoreService;

    @Autowired
    FullScoreService fullScoreService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        List<String> subjects = subjectService.querySubjects(projectId);
        ArrayList<String> subjectCombinations = subjectCombinationService.getAllSubjectCombinations(projectId);
        Range provinceRange = Range.province(provinceService.getProjectProvince(projectId));
        List<Document> projectSchools = schoolService.getProjectSchools(projectId);

        //科目
        AtomicReference<List<Map<String, Object>>> subjectList = new AtomicReference<>(new ArrayList<>());
        subjects.forEach(subject -> {
            Map<String, Object> map = getAverageMap(projectId, provinceRange, projectSchools, subject);
            subjectList.get().add(map);
        });

        //组合科目
        AtomicReference<List<Map<String, Object>>> subjectCombinationList = new AtomicReference<>(new ArrayList<>());
        subjectCombinations.forEach(subjectCombination -> {
            Map<String, Object> map = getAverageMap(projectId, provinceRange, projectSchools, subjectCombination);
            subjectCombinationList.get().add(map);
        });

        return Result.success().set("subjects", subjectList.get()).set("subjectCombinations", subjectCombinationList.get());
    }

    public Map<String, Object> getAverageMap(String projectId, Range provinceRange, List<Document> projectSchools, String subject) {
        Target target = targetService.getTarget(projectId, subject);
        Map<String, Object> map = handleData(projectId, provinceRange, target);
        double provinceAverage = MapUtils.getDouble(map, "average");
        Map<String, Object> topSchoolMap = getTopSchoolMap(projectId, projectSchools, target);
        double topSchoolAverage = MapUtils.getDouble(topSchoolMap, "topAverage");
        map.put("topSchool", topSchoolMap);
        map.put("subAverage", DoubleUtils.round(topSchoolAverage - provinceAverage));
        return map;
    }

    private Map<String, Object> handleData(String projectId, Range provinceRange, Target target) {
        //最高分
        double[] minMaxScore = minMaxScoreService.getMinMaxScore(projectId, provinceRange, target);
        double max = minMaxScore[1];
        //平均分
        double average = averageService.getAverage(projectId, provinceRange, target);
        //得分率
        double fullScore = fullScoreService.getFullScore(projectId, target);
        double scoreRate = DoubleUtils.round(average / fullScore, true);
        Map<String, Object> map = new HashMap<>();
        String sid = target.getId().toString();
        map.put("subjectId", sid);
        map.put("subjectName", SubjectService.getSubjectName(sid));
        map.put("maxScore", max);
        map.put("average", DoubleUtils.round(average));
        map.put("scoreRate", scoreRate);
        return map;
    }

    private Map<String, Object> getTopSchoolMap(String projectId, List<Document> projectSchools, Target target) {
        List<Map<String, Object>> list = new ArrayList<>();
        projectSchools.forEach(school -> {
            String schoolId = school.getString("school");
            String schoolName = schoolService.getSchoolName(projectId, schoolId);
            double average = averageService.getAverage(projectId, Range.school(schoolId), target);
            Map<String, Object> map = new HashMap<>();
            map.put("schoolId", schoolId);
            map.put("schoolName", schoolName);
            map.put("topAverage", DoubleUtils.round(average));
            list.add(map);
        });
        Collections.sort(list, (Map<String, Object> m1, Map<String, Object> m2) -> {
            Double d1 = MapUtils.getDouble(m1, "topAverage");
            Double d2 = MapUtils.getDouble(m2, "topAverage");
            return d2.compareTo(d1);
        });
        return list.isEmpty() ? Collections.emptyMap() : list.get(0);
    }
}
