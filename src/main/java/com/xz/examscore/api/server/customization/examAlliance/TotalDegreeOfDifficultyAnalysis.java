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
public class TotalDegreeOfDifficultyAnalysis implements Server{

    @Autowired
    ProjectService projectService;

    @Autowired
    ProvinceService provinceService;

    @Autowired
    SubjectService subjectService;

    @Autowired
    SubjectCombinationService subjectCombinationService;

    @Autowired
    FullScoreService fullScoreService;

    @Autowired
    AverageService averageService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String province = provinceService.getProjectProvince(projectId);
        Range provinceRange = Range.province(province);
        List<String> subjectIds = subjectService.querySubjects(projectId);
        List<String> combinedSubjectIds = subjectCombinationService.getAllSubjectCombinations(projectId);
        List<Map<String, Object>> result = new ArrayList<>();

        subjectIds.forEach(subjectId -> {
            Target target = Target.subject(subjectId);
            result.add(getDegreeOfDifficulty(projectId, provinceRange, target));
        });

        combinedSubjectIds.forEach(combinedSubjectId -> {
            Target target = Target.subjectCombination(combinedSubjectId);
            result.add(getDegreeOfDifficulty(projectId, provinceRange, target));
        });

        return Result.success().set("totalDegreeOfDifficulty", result);
    }

    public Map<String, Object> getDegreeOfDifficulty(String projectId, Range provinceRange, Target target) {
        Map<String, Object> resultMap = new HashMap<>();
        String subjectId = target.getId().toString();
        String subjectName = SubjectService.getSubjectName(subjectId);
        double average = averageService.getAverage(projectId, provinceRange, target);
        double fullScore = fullScoreService.getFullScore(projectId, target);
        double rate = DoubleUtils.round(average / fullScore, true);
        resultMap.put("subjectId", subjectId);
        resultMap.put("subjectName", subjectName);
        resultMap.put("rate", rate);
        return resultMap;
    }
}
