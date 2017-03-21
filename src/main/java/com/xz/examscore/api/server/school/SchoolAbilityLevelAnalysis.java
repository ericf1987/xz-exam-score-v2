package com.xz.examscore.api.server.school;

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
@Function(description = "学校成绩-能力层级分析", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "subjectId", type = Type.String, description = "科目id,默认第一个科目", required = false),
        @Parameter(name = "schoolId", type = Type.String, description = "学校ID", required = true),
        @Parameter(name = "authSubjectIds", type = Type.StringArray, description = "可访问科目范围，为空返回所有", required = false)
})
@Service
public class SchoolAbilityLevelAnalysis implements Server{

    @Autowired
    SubjectService subjectService;

    @Autowired
    ProjectService projectService;

    @Autowired
    AbilityLevelService abilityLevelService;

    @Autowired
    FullScoreService fullScoreService;

    @Autowired
    ClassAbilityLevelAnalysis classAbilityLevelAnalysis;

    @Autowired
    ClassService classService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String subjectId = param.getString("subjectId");
        String schoolId = param.getString("schoolId");
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

        List<Map<String, Object>> schoolAbilityLevelAnalysis = getSchoolAbilityLevelAnalysis(projectId, schoolId, subjectId, levelMap);

        List<Map<String, Object>> classAbilityLevelAnalysis = getClassAbilityLevelAnalysis(projectId, schoolId, subjectId, levelMap);
        return Result.success().set("schools", schoolAbilityLevelAnalysis).set("classes", classAbilityLevelAnalysis);
    }

    private List<Map<String, Object>> getSchoolAbilityLevelAnalysis(String projectId, String schoolId, String subjectId, Map<String, Document> levelMap) {
        Range schoolRange = Range.school(schoolId);
        return classAbilityLevelAnalysis.getLevelStats(projectId, subjectId, schoolRange, levelMap);
    }

    private List<Map<String, Object>> getClassAbilityLevelAnalysis(String projectId, String schoolId, String subjectId, Map<String, Document> levelMap) {
        List<Document> listClasses = classService.listClasses(projectId, schoolId);

        List<Map<String, Object>> result = new ArrayList<>();
        for(Document classDoc : listClasses){
            String classId = classDoc.getString("class");
            String className = classDoc.getString("name");
            List<Map<String, Object>> levelStats = classAbilityLevelAnalysis.getLevelStats(projectId, subjectId, Range.clazz(classId), levelMap);
            Map<String, Object> map = new HashMap<>();
            map.put("classId", classId);
            map.put("className", className);
            map.put("levelStats", levelStats);
            result.add(map);
        }
        return result;
    }
}
