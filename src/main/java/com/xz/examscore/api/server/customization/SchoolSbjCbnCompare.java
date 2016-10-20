package com.xz.examscore.api.server.customization;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.AverageService;
import com.xz.examscore.services.ProvinceService;
import com.xz.examscore.services.RankService;
import com.xz.examscore.services.SchoolService;
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
public class SchoolSbjCbnCompare implements Server {
    @Autowired
    SchoolService schoolService;

    @Autowired
    ProvinceService provinceService;

    @Autowired
    AverageService averageService;

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
            double average = averageService.getAverage(projectId, Range.school(schoolId), subjectCombinationTarget);
            int rank = rankService.getRank(projectId, Range.province(provinceService.getProjectProvince(projectId)), subjectCombinationTarget, average);
            Map<String, Object> schoolMap = new HashMap<>();
            schoolMap.put("schoolName", schoolDoc.getString("name"));
            schoolMap.put("average", average);
            schoolMap.put("rank", rank);
            result.add(schoolMap);
        });
        return new Result().set("schools", result);
    }
}
