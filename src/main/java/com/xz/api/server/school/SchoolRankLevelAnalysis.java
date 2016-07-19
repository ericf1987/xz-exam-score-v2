package com.xz.api.server.school;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.api.annotation.Function;
import com.xz.api.annotation.Parameter;
import com.xz.api.annotation.Type;
import com.xz.api.server.Server;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author by fengye on 2016/7/18.
 */
@Function(description = "学校成绩-排名统计", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "subjectId", type = Type.String, description = "科目ID", required = false),
        @Parameter(name = "schoolId", type = Type.String, description = "学校id", required = true)
})
@Service
public class SchoolRankLevelAnalysis implements Server{

    @Autowired
    TargetService targetService;

    @Autowired
    SchoolService schoolService;

    @Autowired
    ClassService classService;

    @Autowired
    StudentService studentService;

    @Autowired
    RankLevelService rankLevelService;

    @Autowired
    ProjectConfigService projectConfigService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String subjectId = param.getString("subjectId");
        String schoolId = param.getString("schoolId");

        Target target = targetService.getTarget(projectId, subjectId);

        Map<String, Object> school = getSchoolMap(projectId, target, schoolId);
        List<Map<String, Object>> classes = getClassList(projectId, target, schoolId);
        return Result.success().set("school", school).set("classes", classes);
    }

    private Map<String, Object> getSchoolMap(String projectId, Target target, String schoolId) {
        Range schoolRange =  Range.school(schoolId);
        Map<String, Object> schoolMap = new HashMap<>();
        String schoolName = schoolService.getSchoolName(projectId, schoolId);
        int studentCount = studentService.getStudentCount(projectId, Range.school(schoolId));
        List<Map<String, Object>> rankLevels = sort(rankLevelService.getRankLevelMap(projectId, schoolRange, target));
        schoolMap.put("studentCount", studentCount);
        schoolMap.put("schoolName", schoolName);
        schoolMap.put("schoolId", schoolId);
        schoolMap.put("rankLevels", rankLevels);
        return schoolMap;
    }

    private List<Map<String, Object>> getClassList(String projectId, Target target, String schoolId){
        List<Map<String, Object>> classes = new ArrayList<>();
        List<String> classIds = classService.listClasses(projectId, schoolId).stream()
                .map(document -> document.getString("class")).collect(Collectors.toList());

        for(String classId : classIds){
            Map<String, Object> classMap = new HashMap<>();
            Range classRange = Range.clazz(classId);
            String className = classService.getClassName(projectId, classId);
            int studentCount = studentService.getStudentCount(projectId, Range.clazz(classId));
            List<Map<String, Object>> rankLevels = sort(rankLevelService.getRankLevelMap(projectId, classRange, target));

            classMap.put("studentCount", studentCount);
            classMap.put("className", className);
            classMap.put("classId", classId);
            classMap.put("rankLevels", rankLevels);
            classes.add(classMap);
        }

        return classes;
    }

    private List<Map<String, Object>> sort(List<Map<String, Object>> list){
        Collections.sort(list, (Map<String, Object> m1, Map<String, Object> m2) ->
            m1.get("rankLevel").toString().compareTo(m2.get("rankLevel").toString())
        );
        return list;
    }

}
