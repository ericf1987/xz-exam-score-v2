package com.xz.examscore.api.server.customization.examAlliance;

import com.xz.ajiaedu.common.lang.Result;
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
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author by fengye on 2016/12/28.
 */
@Function(description = "联考项目-临界生人数及各科得分率", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true)
})
@Service
public class ToBeEntryLevelStudentAnalysis implements Server {

    @Autowired
    StudentService studentService;

    @Autowired
    CollegeEntryLevelService collegeEntryLevelService;

    @Autowired
    SubjectService subjectService;

    @Autowired
    ProvinceService provinceService;

    @Autowired
    SchoolService schoolService;

    @Autowired
    ScoreService scoreService;

    @Autowired
    FullScoreService fullScoreService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        Range projectRange = Range.province(provinceService.getProjectProvince(projectId));
        Target target = Target.project(projectId);
        List<String> subjects = subjectService.querySubjects(projectId);

        //获取临界生
        List<String> studentIds = getTotalToBeEntryLevelStu(projectId, projectRange, target);
        Map<String, Object> projectData = handleData(projectId, projectRange, studentIds, subjects);

        List<Map<String, Object>> schoolData = handleSchoolData(projectId, studentIds, "school", subjects);

        return Result.success().set("projectData", projectData).set("schoolData", schoolData);
    }

    private Map<String, Object> handleData(String projectId, Range range, List<String> studentIds, List<String> subjects) {
        Map<String, Object> resultMap = new HashMap<>();
        List<Map<String, Object>> subjectData = new ArrayList<>();
        resultMap.put("count", studentIds.size());
        String schoolName = schoolService.getSchoolName(projectId, range.getId());
        resultMap.put("schoolId", range.getId());
        resultMap.put("schoolName", StringUtils.isEmpty(schoolName) ? "总体" : schoolName);
        for(String subjectId : subjects){
            Map<String, Object> subjectMap = new HashMap<>();
            double fullScore = fullScoreService.getFullScore(projectId, Target.subject(subjectId));
            double subjectTotal = 0;
            double subjectFull = 0;
            for(String studentId : studentIds){
                double score = scoreService.getScore(projectId, Range.student(studentId), Target.subject(subjectId));
                subjectTotal += score;
                subjectFull += fullScore;
            }
            double averageRate = DoubleUtils.round(subjectTotal / subjectFull, true);
            subjectMap.put("subjectId", subjectId);
            subjectMap.put("subjectName", SubjectService.getSubjectName(subjectId));
            subjectMap.put("averageRate", averageRate);
            subjectData.add(subjectMap);
        }
        resultMap.put("subjects", subjectData);
        return resultMap;
    }

    private List<Map<String, Object>> handleSchoolData(String projectId, List<String> studentIds, String rangeName, List<String> subjects) {
        ArrayList<Document> documents = studentService.pickStudentsByRange(projectId, studentIds, rangeName);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Document doc : documents){
            String schoolId = doc.getString("_id");
            Range schoolRange = Range.school(schoolId);
            List<String> ids = (List<String>)doc.get("students");
            Map<String, Object> map = handleData(projectId, schoolRange, ids, subjects);
            result.add(map);
        }
        return result;
    }

    public List<String> getTotalToBeEntryLevelStu(String projectId, Range projectRange, Target target) {
        List<Document> entryLevelDoc = collegeEntryLevelService.getEntryLevelDoc(projectId);
        double entryLevelOne = entryLevelDoc.stream().filter(doc -> doc.getString("level").equals("ONE"))
                .mapToDouble(doc -> doc.getDouble("score")).sum();
        //比一本线低15分为临界分数线
        double scoreLine = entryLevelOne - ToBeEntryLevelAnalysis.ENTRY_LEVEL_SCORE_TO_BE;
        ArrayList<Document> studentByKey = collegeEntryLevelService.getEntryLevelStudentByKey(projectId, projectRange, target, "TWO");
        List<Document> requiredStudent = studentByKey.stream().filter(student -> student.getDouble("totalScore") >= scoreLine).collect(Collectors.toList());
        return requiredStudent.stream().map(student -> student.getString("student")).collect(Collectors.toList());
    }
}
