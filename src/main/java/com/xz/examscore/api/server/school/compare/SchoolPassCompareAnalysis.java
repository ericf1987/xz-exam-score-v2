package com.xz.examscore.api.server.school.compare;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.report.Keys;
import com.xz.examscore.api.Param;
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

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author by fengye on 2016/7/22.
 */

@SuppressWarnings("unchecked")
@Function(description = "学校成绩-合格率对比", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "subjectId", type = Type.String, description = "科目ID", required = false),
        @Parameter(name = "schoolId", type = Type.String, description = "学校id", required = true)
})
@Service
public class SchoolPassCompareAnalysis implements Server {

    @Autowired
    SchoolService schoolService;

    @Autowired
    ClassService classService;

    @Autowired
    ProjectService projectService;

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
        //对比类报表只对比同年级数据，即只对比当前考试项目下班级的历次考试
        List<Document> classDocs = classService.listClasses(projectId, schoolId);
        Document doc = projectService.findProject(projectId);
        List<Document> projectDocs = projectService.listProjectsByRange(Range.clazz(classDocs.get(0).getString("class")), doc.getString("category"));

        //学校考试列表
        //List<Document> projectList = projectService.listProjectsByRange(Range.school(schoolId));
        projectDocs = projectDocs.stream().filter(projectDoc -> null != projectDoc && !projectDoc.isEmpty()).collect(Collectors.toList());
        Collections.sort(projectDocs, (Document d1, Document d2) -> d1.getString("startDate").compareTo(d2.getString("startDate")));

        Map<String, Object> schoolPassRateMap = getSchoolPassRateMap(projectId, schoolId, subjectId, projectDocs);
        List<Map<String, Object>> classPassRateList = getClassPassRateList(projectId, schoolId, subjectId, projectDocs);
        Collections.sort(classPassRateList, (Map<String, Object> m1, Map<String, Object> m2) -> m1.get("className").toString().compareTo(m2.get("className").toString()));

        return Result.success()
                .set("school", schoolPassRateMap)
                .set("classes", classPassRateList)
                .set("projectList", projectDocs)
                .set("hasHeader", !schoolPassRateMap.isEmpty());
    }

    private Map<String, Object> getSchoolPassRateMap(String projectId, String schoolId, String subjectId, List<Document> projectList) {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, Object>> excellents = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = format.format(Calendar.getInstance().getTime());
        //查询学校的考试记录
        String schoolName = schoolService.getSchoolName(projectId, schoolId);

        for (Document projectDoc : projectList) {
            Map<String, Object> excellent = new HashMap<>();
            String startDate = projectDoc.getString("startDate") == null ? currentDate : projectDoc.getString("startDate");
            String projectName = projectDoc.getString("name");
            Target target = targetService.getTarget(projectDoc.getString("project"), subjectId);
            List<Document> scoreLevels = scoreLevelService.getScoreLevelRate(projectDoc.getString("project"), Range.school(schoolId), target);
            double rate = getScoreLevelRate(scoreLevels, Keys.ScoreLevel.Pass);
            excellent.put("projectName", projectName);
            excellent.put("startDate", startDate);
            excellent.put("rate", DoubleUtils.round(rate, true));
            excellents.add(excellent);
        }

        map.put("schoolId", schoolId);
        map.put("schoolName", schoolName);
        map.put("passes", excellents);

        return map;
    }

    private List<Map<String, Object>> getClassPassRateList(String projectId, String schoolId, String subjectId, List<Document> projectList) {
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
                Target target = targetService.getTarget(projectDoc.getString("project"), subjectId);
                List<Document> scoreLevels = scoreLevelService.getScoreLevelRate(projectDoc.getString("project"), Range.clazz(classId), target);
                double rate = getScoreLevelRate(scoreLevels, Keys.ScoreLevel.Pass);
                excellent.put("projectName", projectDoc.getString("name"));
                excellent.put("startDate", startDate);
                excellent.put("rate", DoubleUtils.round(rate, true));
                excellents.add(excellent);
            });
            Map<String, Object> map = new HashMap<>();
            map.put("classId", classId);
            map.put("className", className);
            map.put("passes", excellents);
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
