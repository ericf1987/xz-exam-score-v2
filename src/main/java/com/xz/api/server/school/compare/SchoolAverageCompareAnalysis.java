package com.xz.api.server.school.compare;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.api.annotation.Function;
import com.xz.api.annotation.Parameter;
import com.xz.api.annotation.Type;
import com.xz.api.server.Server;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.services.*;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author by fengye on 2016/7/22.
 */

@SuppressWarnings("unchecked")
@Function(description = "学校成绩-平均分对比", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "subjectId", type = Type.String, description = "科目ID", required = false),
        @Parameter(name = "schoolId", type = Type.String, description = "学校id", required = true)
})
@Service
public class SchoolAverageCompareAnalysis implements Server {

    @Autowired
    SchoolService schoolService;

    @Autowired
    ClassService classService;

    @Autowired
    ProjectService projectService;

    @Autowired
    AverageService averageService;

    @Autowired
    TargetService targetService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String schoolId = param.getString("schoolId");
        String subjectId = param.getString("subjectId");
        return getResult(projectId, schoolId, subjectId);
    }

    public Result getResult(String projectId, String schoolId, String subjectId) {
        //学校考试列表
        List<Document> projectList = projectService.listProjectsByRange(Range.school(schoolId));
        projectList = projectList.stream().filter(projectDoc -> null != projectDoc && !projectDoc.isEmpty()).collect(Collectors.toList());

        Target target = targetService.getTarget(projectId, subjectId);

        Map<String, Object> schoolAverageMap = getSchoolAverageMap(projectId, schoolId, target, projectList);
        List<Map<String, Object>> classAverageList = getClassAverageList(projectId, schoolId, target, projectList);
        return Result.success()
                .set("school", schoolAverageMap)
                .set("classes", classAverageList)
                .set("hasHeader", !schoolAverageMap.isEmpty());
    }

    private Map<String, Object> getSchoolAverageMap(String projectId, String schoolId, Target target, List<Document> projectList) {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, Object>> averages = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = format.format(Calendar.getInstance().getTime());


        //查询学校的考试记录
        String schoolName = schoolService.getSchoolName(projectId, schoolId);
        projectList.stream().forEach(projectDoc -> {
            Map<String, Object> average = new HashMap<>();
            String startDate = projectDoc.getString("startDate") == null ? currentDate : projectDoc.getString("startDate");
            String projectName = projectDoc.getString("name");
            double score = averageService.getAverage(projectDoc.getString("project"), Range.school(schoolId), target);
            average.put("projectName", projectName);
            average.put("startDate", startDate);
            average.put("score", score);
            averages.add(average);
        });

        map.put("schoolId", schoolId);
        map.put("schoolName", schoolName);
        map.put("averages", averages);

        return map;
    }

    private List<Map<String, Object>> getClassAverageList(String projectId, String schoolId, Target target, List<Document> projectList) {
        List<Document> classList = classService.listClasses(projectId, schoolId);
        List<Map<String, Object>> classes = new ArrayList<>();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = format.format(Calendar.getInstance().getTime());

        for (Document classDoc : classList) {
            List<Map<String, Object>> averages = new ArrayList<>();
            String classId = classDoc.getString("class");
            String className = classDoc.getString("name");
            projectList.stream().forEach(projectDoc -> {
                Map<String, Object> average = new HashMap<>();
                String startDate = projectDoc.getString("startDate") == null ? currentDate : projectDoc.getString("startDate");
                double score = averageService.getAverage(projectDoc.getString("project"), Range.clazz(classId), target);
                average.put("projectName", projectDoc.getString("name"));
                average.put("startDate", startDate);
                average.put("score", score);
                averages.add(average);
            });
            Map<String, Object> map = new HashMap<>();
            map.put("classId", classId);
            map.put("className", className);
            map.put("averages", averages);
            classes.add(map);
        }
        return classes;
    }
}
