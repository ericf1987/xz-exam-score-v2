package com.xz.examscore.api.server.project;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.api.server.classes.ClassAbilityLevelAnalysis;
import com.xz.examscore.bean.Range;
import com.xz.examscore.services.*;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xz.examscore.api.server.classes.ClassPointAnalysis.initSubject;
import static com.xz.examscore.api.server.project.ProjectPointAbilityLevelAnalysis.filterLevels;

/**
 * @author by fengye on 2017/3/20.
 */
@Function(description = "总体成绩-能力层级分析", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "subjectId", type = Type.String, description = "科目ID", required = true),
        @Parameter(name = "schoolIds", type = Type.StringArray, description = "学校id列表", required = true),
        @Parameter(name = "authSubjectIds", type = Type.StringArray, description = "可访问科目范围，为空返回所有", required = false)
})
@Service
public class ProjectAbilityLevelAnalysis implements Server{

    @Autowired
    SubjectService subjectService;

    @Autowired
    AbilityLevelService abilityLevelService;

    @Autowired
    ProjectService projectService;

    @Autowired
    FullScoreService fullScoreService;
    
    @Autowired
    ProvinceService provinceService;

    @Autowired
    ClassAbilityLevelAnalysis classAbilityLevelAnalysis;

    @Autowired
    SchoolService schoolService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String subjectId = param.getString("subjectId");
        String[] schoolIds = param.getStringValues("schoolIds");
        String[] authSubjectIds = param.getStringValues("authSubjectIds");

        if(StringUtil.isBlank(subjectId)){
            subjectId = initSubject(projectId, authSubjectIds, subjectService);
        }

        if (StringUtil.isBlank(subjectId)) {
            return Result.fail("找不到考试科目信息");
        }

        String studyStage = projectService.findProjectStudyStage(projectId);
        Map<String, Document> levelMap = abilityLevelService.queryAbilityLevels(studyStage, subjectId);
        levelMap = filterLevels(projectId, subjectId, levelMap, fullScoreService);

        String province = provinceService.getProjectProvince(projectId);

        List<Map<String, Object>> provinceAbilityLevelAnalysis = getProvinceAbilityLevelAnalysis(projectId, province, subjectId, levelMap);

        List<Map<String, Object>> schoolAbilityLevelAnalysis = getSchoolAbilityLevelAnalysis(projectId, schoolIds, subjectId, levelMap);

        return Result.success().set("province", provinceAbilityLevelAnalysis).set("schools", schoolAbilityLevelAnalysis);
    }

    private List<Map<String, Object>> getProvinceAbilityLevelAnalysis(String projectId, String province, String subjectId, Map<String, Document> levelMap) {
        Range provinceRange = Range.province(province);
        return classAbilityLevelAnalysis.getLevelStats(projectId, subjectId, provinceRange, levelMap);
    }

    private List<Map<String, Object>> getSchoolAbilityLevelAnalysis(String projectId, String[] schoolIds, String subjectId, Map<String, Document> levelMap) {
        List<Map<String, Object>> result = new ArrayList<>();
        for(String schoolId : schoolIds){
            String schoolName = schoolService.getSchoolName(projectId, schoolId);
            List<Map<String, Object>> levelStats = classAbilityLevelAnalysis.getLevelStats(projectId, subjectId, Range.school(schoolId), levelMap);
            Map<String, Object> map = new HashMap<>();
            map.put("schoolId", schoolId);
            map.put("schoolName", schoolName);
            map.put("levelStats", levelStats);
            result.add(map);
        }
        return result;
    }
}
