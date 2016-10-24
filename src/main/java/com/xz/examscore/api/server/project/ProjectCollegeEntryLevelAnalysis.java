package com.xz.examscore.api.server.project;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.mongo.DocumentUtils;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.*;
import org.apache.commons.lang.math.NumberUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author by fengye on 2016/10/24.
 */
@Function(description = "总体成绩-总体本科录取情况", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "rankSegment", type = Type.StringArray, description = "排名分段", required = true),
        @Parameter(name = "authSubjectIds", type = Type.StringArray, description = "可访问科目范围，为空返回所有", required = false)
})
@Service
public class ProjectCollegeEntryLevelAnalysis implements Server{
    private static final Logger LOG = LoggerFactory.getLogger(ProjectCollegeEntryLevelAnalysis.class);

    @Autowired
    RangeService rangeService;

    @Autowired
    SubjectService subjectService;

    @Autowired
    StudentService studentService;

    @Autowired
    ClassService classService;

    @Autowired
    SchoolService schoolService;

    @Autowired
    CollegeEntryLevelService collegeEntryLevelService;

    @Autowired
    ProjectTopStudentStat projectTopStudentStat;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String[] rankSegment = param.getStringValues("rankSegment");
        String[] authSubjectIds = param.getStringValues("authSubjectIds");

        Range range = rangeService.queryProvinceRange(projectId);
        Target target = Target.project(projectId);
        List<String> subjectIds = new ArrayList<>(subjectService.querySubjects(projectId));
        subjectIds = ProjectTopStudentStat.filterSubject(subjectIds, authSubjectIds);
        subjectIds.sort(String::compareTo);

        List<Map<String, Object>> students = getEntryLevelStudents(projectId, rankSegment, range, target, subjectIds);
        return Result.success().set("students", students).set("hasHeader", !students.isEmpty());
    }

    public List<Map<String, Object>> getEntryLevelStudents(String projectId, String[] rankSegment, Range range, Target target, List<String> subjectIds) {
        int minIndex = NumberUtils.toInt(rankSegment[0]);
        int maxIndex = NumberUtils.toInt(rankSegment[1]);

        List<Map<String, Object>> result = new ArrayList<>();
        List<Document> students = collegeEntryLevelService.getEntryLevelStudent(projectId, range, target, minIndex, maxIndex);
        for (Document studentDoc : students){
            Map<String, Object> map = new HashMap<>();
            String studentId = studentDoc.getString("student");
            double totalScore = DocumentUtils.getDouble(studentDoc, "totalScore", 0);
            int rank = DocumentUtils.getInt(studentDoc, "rank", 0);
            double dValue = DocumentUtils.getDouble(studentDoc, "dValue", 0);
            Map<String, Object> entry_level = (Map<String, Object>)studentDoc.get("college_entry_level");
            String info = getEntryLevelInfo(entry_level, dValue);

            Document student = studentService.findStudent(projectId, studentId);
            if (student == null) {
                LOG.warn("找不到学生'" + studentId + "'的考试'" + projectId + "'记录");
                continue;
            }

            String schoolId = student.getString("school");
            String classId = student.getString("class");
            //学生本次考试考号
            map.put("examNo", student.getString("examNo"));
            map.put("name", student.getString("name"));
            map.put("totalScore", totalScore);
            map.put("entryLevelInfo", info);

            if (range.match(Range.PROVINCE)) {
                map.put("schoolId", schoolId);
                map.put("schoolName", schoolService.getSchoolName(projectId, schoolId));
            } else {
                map.put("classId", classId);
                map.put("className", classService.getClassName(projectId, classId));
            }

            // 总分分析
            List<Map<String, Object>> list = new ArrayList<>();
            Map<String, Object> projectInfo = new HashMap<>();
            projectInfo.put("score", totalScore);
            projectInfo.put("rankIndex", rank);
            projectInfo.put("subjectId", "000");
            projectInfo.put("subjectName", "总体");
            list.add(projectInfo);

            // 科目统计
            projectTopStudentStat.subjectStat(projectId, subjectIds, studentId, list, range);

            map.put("subjects", list);
            result.add(map);

        }

        result.sort((o1, o2) -> ((Double) o2.get("totalScore")).compareTo((Double) o1.get("totalScore")));
        return result;
    }

    private String getEntryLevelInfo(Map<String, Object> entry_level, double dValue) {
        String level = entry_level.get("level").toString();
        switch (level) {
            case "ONE":
                return "超一本线" + dValue + "分";
            case "TWO":
                return "超二本线" + dValue + "分";
            case "THREE":
                return "超三本线" + dValue + "分";
        }
        return "";
    }
}
