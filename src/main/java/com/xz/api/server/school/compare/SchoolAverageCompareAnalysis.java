package com.xz.api.server.school.compare;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.api.annotation.Function;
import com.xz.api.annotation.Parameter;
import com.xz.api.annotation.Type;
import com.xz.api.server.Server;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.services.AverageService;
import com.xz.services.ClassService;
import com.xz.services.ProjectService;
import com.xz.services.SchoolService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author by fengye on 2016/7/22.
 */

@SuppressWarnings("unchecked")
@Function(description = "学校成绩-平均分对比", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "subjectId", type = Type.String, description = "科目ID", required = true),
        @Parameter(name = "schoolId", type = Type.String, description = "学校id", required = true)
})
@Service
public class SchoolAverageCompareAnalysis implements Server{

    @Autowired
    SchoolService schoolService;

    @Autowired
    ClassService classService;

    @Autowired
    ProjectService projectService;

    @Autowired
    AverageService averageService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String schoolId = param.getString("schoolId");
        String subjectId = param.getString("subjectId");

        Map<String, Object> schoolAverageMap = getSchoolAverageMap(projectId, schoolId, subjectId);
        List<Map<String, Object>> classAverageList = getClassAverageList(projectId, schoolId, subjectId);
        Result result = Result.success().set("school", schoolAverageMap).set("classes", classAverageList);
        System.out.println(result.getData());
        return result;
    }

    private Map<String,Object> getSchoolAverageMap(String projectId, String schoolId, String subjectId) {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, Object>> averages = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = format.format(Calendar.getInstance().getTime());
        //查询学校的考试记录
        String schoolName = schoolService.getSchoolName(projectId, schoolId);
        List<Document> projectList = projectService.listProjectsByRange(Range.school(schoolId));
        for(Document projectDoc : projectList){
            if(null != projectDoc && !projectDoc.isEmpty()){
                Map<String, Object> average = new HashMap<>();
                String startDate = projectDoc.getString("startDate") == null ? currentDate : projectDoc.getString("startDate");
                String projectName = projectDoc.getString("name");
                double score = averageService.getAverage(projectId, Range.school(schoolId), Target.subject(subjectId));
                average.put("projectName", projectName);
                average.put("startDate", startDate);
                average.put("score", score);
                averages.add(average);
            }
        }

        map.put("schoolId", schoolId);
        map.put("schoolName", schoolName);
        map.put("averages", averages);

        return map;
    }

    private List<Map<String,Object>> getClassAverageList(String projectId, String schoolId, String subjectId) {
        List<Document> classList = classService.listClasses(projectId, schoolId);
        System.out.println("班级列表-->" + classList.toString());
        List<Map<String, Object>> averages = new ArrayList<>();

        List<Map<String, Object>> classes = new ArrayList<>();

        for (Document classDoc : classList){
            String classId = classDoc.getString("class");
            String className = classDoc.getString("name");
            List<Document> projectList = projectService.listProjectsByRange(Range.clazz(classId));
            System.out.println("班级考试列表-->" + projectList.toString());
            for(Document projectDoc : projectList){
                if(null != projectDoc && !projectDoc.isEmpty()) {
                    Map<String, Object> average = new HashMap<>();
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    String currentDate = format.format(Calendar.getInstance().getTime());
                    String startDate = projectDoc.getString("startDate") == null ? currentDate : projectDoc.getString("startDate");
                    String projectName = projectDoc.getString("name");
                    double score = averageService.getAverage(projectId, Range.clazz(classId), Target.subject(subjectId));
                    average.put("projectName", projectName);
                    average.put("startDate", startDate);
                    average.put("score", score);
                    averages.add(average);
                }
            }
            Map<String, Object> map = new HashMap<>();
            map.put("classId", classId);
            map.put("className", className);
            map.put("averages", averages);
            classes.add(map);
        }
        return classes;
    }
}