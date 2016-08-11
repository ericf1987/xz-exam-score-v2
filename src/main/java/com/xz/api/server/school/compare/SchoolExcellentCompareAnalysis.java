package com.xz.api.server.school.compare;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.report.Keys;
import com.xz.api.Param;
import com.xz.api.annotation.Function;
import com.xz.api.annotation.Parameter;
import com.xz.api.annotation.Type;
import com.xz.api.server.Server;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.services.*;
import com.xz.util.DoubleUtils;
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
@Function(description = "学校成绩-优秀率对比", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "subjectId", type = Type.String, description = "科目ID", required = false),
        @Parameter(name = "schoolId", type = Type.String, description = "学校id", required = true)
})
@Service
public class SchoolExcellentCompareAnalysis implements Server {

    @Autowired
    SchoolService schoolService;

    @Autowired
    ClassService classService;

    @Autowired
    ProjectService projectService;

    @Autowired
    AverageService averageService;

    @Autowired
    ScoreLevelService scoreLevelService;

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

        Map<String, Object> schoolExcellentMap = getSchoolExcellentMap(projectId, schoolId, target, projectList);
        List<Map<String, Object>> classExcellentList = getClassExcellentList(projectId, schoolId, target, projectList);
        return Result.success()
                .set("school", schoolExcellentMap)
                .set("classes", classExcellentList)
                .set("hasHeader", !schoolExcellentMap.isEmpty());
    }

    private Map<String, Object> getSchoolExcellentMap(String projectId, String schoolId, Target target, List<Document> projectList) {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, Object>> excellents = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = format.format(Calendar.getInstance().getTime());
        //查询学校的考试记录
        String schoolName = schoolService.getSchoolName(projectId, schoolId);
        projectList.stream().forEach(projectDoc -> {
            Map<String, Object> excellent = new HashMap<>();
            String startDate = projectDoc.getString("startDate") == null ? currentDate : projectDoc.getString("startDate");
            String projectName = projectDoc.getString("name");
            List<Document> scoreLevels = scoreLevelService.getScoreLevelRate(projectDoc.getString("project"), Range.school(schoolId), target);
            double rate = getScoreLevelRate(scoreLevels, Keys.ScoreLevel.Excellent);
            excellent.put("projectName", projectName);
            excellent.put("startDate", startDate);
            excellent.put("rate", DoubleUtils.round(rate, true));
            excellents.add(excellent);
        });

        map.put("schoolId", schoolId);
        map.put("schoolName", schoolName);
        map.put("excellents", excellents);

        return map;
    }

    private List<Map<String, Object>> getClassExcellentList(String projectId, String schoolId, Target target, List<Document> projectList) {
        List<Document> classList = classService.listClasses(projectId, schoolId);

        List<Map<String, Object>> classes = new ArrayList<>();

        for (Document classDoc : classList) {
            List<Map<String, Object>> excellents = new ArrayList<>();
            String classId = classDoc.getString("class");
            String className = classDoc.getString("name");
            projectList.stream().forEach(projectDoc -> {
                Map<String, Object> excellent = new HashMap<>();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                String currentDate = format.format(Calendar.getInstance().getTime());
                String startDate = projectDoc.getString("startDate") == null ? currentDate : projectDoc.getString("startDate");
                List<Document> scoreLevels = scoreLevelService.getScoreLevelRate(projectDoc.getString("project"), Range.clazz(classId), target);
                double rate = getScoreLevelRate(scoreLevels, Keys.ScoreLevel.Excellent);
                excellent.put("projectName", projectDoc.getString("name"));
                excellent.put("startDate", startDate);
                excellent.put("rate", DoubleUtils.round(rate, true));
                excellents.add(excellent);
            });
            Map<String, Object> map = new HashMap<>();
            map.put("classId", classId);
            map.put("className", className);
            map.put("excellents", excellents);
            classes.add(map);
        }
        return classes;
    }

    private double getScoreLevelRate(List<Document> scoreLevels, Keys.ScoreLevel excellent) {
        for (Document doc : scoreLevels) {
            if (doc.getString("scoreLevel").equals(excellent.name())) {
                return DoubleUtils.round(doc.getDouble("rate"), true);
            }
        }
        return 0d;
    }

}
