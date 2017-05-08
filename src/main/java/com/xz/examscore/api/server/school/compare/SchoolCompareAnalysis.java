package com.xz.examscore.api.server.school.compare;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.report.Keys;
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

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author by fengye on 2016/9/26.
 */
@SuppressWarnings("unchecked")
@Function(description = "学校成绩-对比", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "subjectId", type = Type.String, description = "科目ID", required = false),
        @Parameter(name = "schoolId", type = Type.String, description = "学校id", required = true)
})
@Service
public class SchoolCompareAnalysis implements Server{

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

    @Autowired
    AverageService averageService;

    @Autowired
    ScoreService scoreService;

    @Autowired
    FullScoreService fullScoreService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String schoolId = param.getString("schoolId");
        String subjectId = param.getString("subjectId");
        return getResult(projectId, schoolId, subjectId);
    }

    private Result getResult(String projectId, String schoolId, String subjectId) {
        //对比类报表只对比同年级数据，即只对比当前考试项目下班级的历次考试
        List<Document> classDocs = classService.listClasses(projectId, schoolId);
        //只查询所有理科考试或者所有文科考试
        Document doc = projectService.findProject(projectId);

        List<Document> projectDocs = projectService.listProjectsByRange(Range.clazz(classDocs.get(0).getString("class")), doc.getString("category"));

        projectDocs = projectDocs.stream().filter(projectDoc -> null != projectDoc && !projectDoc.isEmpty()).collect(Collectors.toList());
        Collections.sort(projectDocs, (Document d1, Document d2) -> d1.getString("startDate").compareTo(d2.getString("startDate")));

        Map<String, Object> schoolMap = getSchoolMap(projectId, schoolId, subjectId, projectDocs);
        List<Map<String, Object>> classList = getClassList(projectId, schoolId, subjectId, projectDocs);
        Collections.sort(classList, (Map<String, Object> m1, Map<String, Object> m2) -> m1.get("className").toString().compareTo(m2.get("className").toString()));

        return Result.success()
                .set("school", schoolMap)
                .set("classes", classList)
                .set("hasHeader", !schoolMap.isEmpty());
    }

    private List<Map<String, Object>> getClassList(String projectId, String schoolId, String subjectId, List<Document> projectDocs) {
        List<Document> classList = classService.listClasses(projectId, schoolId);

        List<Map<String, Object>> classes = new LinkedList<>();

        for (Document classDoc : classList) {
            List<Map<String, Object>> rates = new LinkedList<>();
            String classId = classDoc.getString("class");
            String className = classDoc.getString("name");
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("classId", classId);
            map.put("className", className);
            projectDocs.stream().forEach(projectDoc -> {
                Map<String, Object> oneRate = new LinkedHashMap<>();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                String currentDate = format.format(Calendar.getInstance().getTime());
                String startDate = projectDoc.getString("startDate") == null ? currentDate : projectDoc.getString("startDate");
                Target target = targetService.getTarget(projectDoc.getString("project"), subjectId);
                List<Document> scoreLevels = scoreLevelService.getScoreLevelRate(projectDoc.getString("project"), Range.clazz(classId), target);
                double pass = getScoreLevelRate(scoreLevels, Keys.ScoreLevel.Pass);
                int passCount = getScoreLevelCount(scoreLevels, Keys.ScoreLevel.Pass);
                double excellent = getScoreLevelRate(scoreLevels, Keys.ScoreLevel.Excellent);
                int excellentCount = getScoreLevelCount(scoreLevels, Keys.ScoreLevel.Excellent);
                double average = averageService.getAverage(projectDoc.getString("project"), Range.clazz(classId), target);
                double fullScore = fullScoreService.getFullScore(projectDoc.getString("project"), target);
                oneRate.put("projectName", projectDoc.getString("name"));
                oneRate.put("startDate", startDate);
                oneRate.put("passRate", DoubleUtils.round(pass, true));
                oneRate.put("passCount", passCount);
                oneRate.put("excellentRate", DoubleUtils.round(excellent, true));
                oneRate.put("excellentCount", excellentCount);
                oneRate.put("average", DoubleUtils.round(average, true));
                oneRate.put("scoreRate", DoubleUtils.round(average/fullScore, true));
                rates.add(oneRate);
            });
            map.put("projects", rates);
            classes.add(map);
        }
        return classes;
    }

    private Map<String,Object> getSchoolMap(String projectId, String schoolId, String subjectId, List<Document> projectDocs) {
        Map<String, Object> map = new LinkedHashMap<>();
        List<Map<String, Object>> rates = new LinkedList<>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = format.format(Calendar.getInstance().getTime());
        //查询学校的考试记录
        String schoolName = schoolService.getSchoolName(projectId, schoolId);
        map.put("schoolId", schoolId);
        map.put("schoolName", schoolName);

        for (Document projectDoc : projectDocs) {
            Map<String, Object> oneRate = new LinkedHashMap<>();
            String startDate = projectDoc.getString("startDate") == null ? currentDate : projectDoc.getString("startDate");
            String projectName = projectDoc.getString("name");
            Target target = targetService.getTarget(projectDoc.getString("project"), subjectId);
            List<Document> scoreLevels = scoreLevelService.getScoreLevelRate(projectDoc.getString("project"), Range.school(schoolId), target);
            double pass = getScoreLevelRate(scoreLevels, Keys.ScoreLevel.Pass);
            int passCount = getScoreLevelCount(scoreLevels, Keys.ScoreLevel.Pass);
            double excellent = getScoreLevelRate(scoreLevels, Keys.ScoreLevel.Excellent);
            int excellentCount = getScoreLevelCount(scoreLevels, Keys.ScoreLevel.Excellent);
            double average = averageService.getAverage(projectDoc.getString("project"), Range.school(schoolId), target);
            double fullScore = fullScoreService.getFullScore(projectDoc.getString("project"), target);

            oneRate.put("projectName", projectName);
            oneRate.put("startDate", startDate);
            oneRate.put("passRate", DoubleUtils.round(pass, true));
            oneRate.put("passCount", passCount);
            oneRate.put("excellentRate", DoubleUtils.round(excellent, true));
            oneRate.put("excellentCount", excellentCount);
            oneRate.put("average", DoubleUtils.round(average, true));
            oneRate.put("scoreRate", DoubleUtils.round(average/fullScore, true));
            rates.add(oneRate);
        }

        map.put("projects", rates);
        return map;
    }

    private double getScoreLevelRate(List<Document> scoreLevels, Keys.ScoreLevel excellent) {
        for (Document doc : scoreLevels) {
            if (doc.getString("scoreLevel").equals(excellent.name())) {
                return DoubleUtils.round(doc.getDouble("rate"), true);
            }
        }
        return 0d;
    }

    private int getScoreLevelCount(List<Document> scoreLevels, Keys.ScoreLevel excellent) {
        for (Document doc : scoreLevels) {
            if (doc.getString("scoreLevel").equals(excellent.name())) {
                return doc.getInteger("count");
            }
        }
        return 0;
    }
}
