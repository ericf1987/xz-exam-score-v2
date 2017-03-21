package com.xz.examscore.api.server.project;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.api.server.classes.ClassPointAnalysis;
import com.xz.examscore.bean.Range;
import com.xz.examscore.services.ProvinceService;
import com.xz.examscore.services.SchoolService;
import com.xz.examscore.services.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author by fengye on 2017/3/20.
 */
@Function(description = "总体成绩-知识点分析", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "subjectId", type = Type.String, description = "科目ID", required = true),
        @Parameter(name = "schoolIds", type = Type.StringArray, description = "学校id列表", required = true),
        @Parameter(name = "authSubjectIds", type = Type.StringArray, description = "可访问科目范围，为空返回所有", required = false)
})
@Service
public class ProjectPointAnalysis implements Server{

    @Autowired
    SubjectService subjectService;

    @Autowired
    ProvinceService provinceService;

    @Autowired
    ClassPointAnalysis classPointAnalysis;

    @Autowired
    SchoolService schoolService;

    @Override
    public Result execute(Param param) throws Exception {

        String projectId = param.getString("projectId");
        String subjectId = param.getString("subjectId");
        String[] schoolIds = param.getStringValues("schoolIds");
        String[] authSubjectIds = param.getStringValues("authSubjectIds");

        // 初始化科目id
        if (StringUtil.isBlank(subjectId)) {
            subjectId = ClassPointAnalysis.initSubject(projectId, authSubjectIds, subjectService);
        }

        if (StringUtil.isBlank(subjectId)) {
            return Result.fail("找不到考试科目信息");
        }

        String province = provinceService.getProjectProvince(projectId);

        Range provinceRange = Range.province(province);

        List<Map<String, Object>> provincePointAnalysis = classPointAnalysis.getPointStats(projectId, subjectId, provinceRange);

        List<Map<String, Object>> schoolPointAnalysis = getSchoolPointAnalysis(projectId, subjectId, schoolIds);

        return Result.success().set("province", provincePointAnalysis).set("schools", schoolPointAnalysis);
    }

    private List<Map<String, Object>> getSchoolPointAnalysis(String projectId, String subjectId, String[] schoolIds) {
        List<Map<String, Object>> result = new ArrayList<>();
        for(String schoolId : schoolIds){
            String schoolName = schoolService.getSchoolName(projectId, schoolId);
            Range schoolRange = Range.school(schoolId);
            List<Map<String, Object>> schoolPointStats = classPointAnalysis.getPointStats(projectId, subjectId, schoolRange);
            Map<String, Object> schoolMap = new HashMap<>();
            schoolMap.put("schoolId", schoolId);
            schoolMap.put("schoolName", schoolName);
            schoolMap.put("pointStats", schoolPointStats);
            result.add(schoolMap);
        }
        return result;
    }
}
