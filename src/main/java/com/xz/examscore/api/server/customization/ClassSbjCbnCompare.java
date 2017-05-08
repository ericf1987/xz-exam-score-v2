package com.xz.examscore.api.server.customization;

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
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author by fengye on 2016/10/20.
 */
@Function(description = "学生组合科目对比", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "subjectCombinationId", type = Type.String, description = "组合科目ID", required = true)
})
@Service
public class ClassSbjCbnCompare implements Server{

    @Autowired
    ClassService classService;

    @Autowired
    SchoolService schoolService;

    @Autowired
    ProvinceService provinceService;

    @Autowired
    AverageService averageService;

    @Autowired
    ScoreService scoreService;

    @Autowired
    RankService rankService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String subjectCombinationId = param.getString("subjectCombinationId");
        Target subjectCombinationTarget = Target.subjectCombination(subjectCombinationId);
        List<Document> schoolDocs = schoolService.getProjectSchools(projectId);
        List<Map<String, Object>> result = new ArrayList<>();
        schoolDocs.forEach(schoolDoc -> {
            String schoolId = schoolDoc.getString("school");
            String schoolName = schoolDoc.getString("name");
            List<Document> classDocs = classService.listClasses(projectId, schoolId);
            classDocs.forEach(classDoc -> {
                String classId = classDoc.getString("class");
                String className = classDoc.getString("name");
                double average = averageService.getAverage(projectId, Range.clazz(classId), subjectCombinationTarget);
                int schoolRank = rankService.getRank(projectId, Range.school(schoolId), subjectCombinationTarget, average);
                int projectRank = rankService.getRank(projectId, Range.province(provinceService.getProjectProvince(projectId)), subjectCombinationTarget, average);
                Map<String, Object> classMap = new HashMap<>();
                classMap.put("schoolName", schoolName);
                classMap.put("className", className);
                classMap.put("average", DoubleUtils.round(average, false));
                classMap.put("schoolRank", schoolRank);
                classMap.put("projectRank", projectRank);
                result.add(classMap);
            });
        });
        return Result.success().set("classes", result);
    }
}
