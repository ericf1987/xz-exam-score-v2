package com.xz.examscore.api.server.classes;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.ajiaedu.common.ajia.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.bean.Point;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.*;
import com.xz.examscore.util.DoubleUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xz.examscore.api.server.project.ProjectTopStudentStat.filterSubject;

/**
 * 班级成绩-知识点分析
 *
 * @author zhaorenwu
 */

@Function(description = "班级成绩-知识点分析", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "subjectId", type = Type.String, description = "科目id,默认第一个科目", required = false),
        @Parameter(name = "classId", type = Type.String, description = "班级id", required = true),
        @Parameter(name = "authSubjectIds", type = Type.StringArray, description = "可访问科目范围，为空返回所有", required = false)
})
@Service
public class ClassPointAnalysis implements Server {

    @Autowired
    SubjectService subjectService;

    @Autowired
    ScoreService scoreService;

    @Autowired
    FullScoreService fullScoreService;

    @Autowired
    StudentService studentService;

    @Autowired
    PointService pointService;

    @Autowired
    AverageService averageService;

    @Autowired
    TargetService targetService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String subjectId = param.getString("subjectId");
        String classId = param.getString("classId");
        String[] authSubjectIds = param.getStringValues("authSubjectIds");

        // 初始化科目id
        if (StringUtil.isBlank(subjectId)) {
            subjectId = initSubject(projectId, authSubjectIds, subjectService);
        }

        if (StringUtil.isBlank(subjectId)) {
            return Result.fail("找不到考试科目信息");
        }

        List<Map<String, Object>> classPointAnalysis = getClassPointAnalysis(projectId, subjectId, classId);
        List<Map<String, Object>> studentPointAnalysis = getStudentPointAnalysis(projectId, subjectId, classId);
        return Result.success()
                .set("classes", classPointAnalysis)
                .set("students", studentPointAnalysis)
                .set("hasHeader", !classPointAnalysis.isEmpty());
    }

    // 学生知识点分析
    private List<Map<String, Object>> getStudentPointAnalysis(String projectId, String subjectId, String classId) {
        List<Map<String, Object>> list = new ArrayList<>();

        List<Document> studentList = studentService.getStudentList(projectId, Range.clazz(classId));
        for (Document student : studentList) {
            Map<String, Object> map = new HashMap<>();

            String studentId = student.getString("student");
            String studentName = student.getString("name");
            map.put("studentId", studentId);
            map.put("studentName", studentName);
            map.put("examNo", student.getString("examNo"));
            map.put("customExamNo", student.getString("customExamNo"));

            Range range = Range.student(studentId);
            map.put("subjectScore", scoreService.getScore(projectId, range, Target.subject(subjectId)));
            map.put("pointStats", getPointStats(projectId, subjectId, range));
            //判断学生是否缺考
            Target target = targetService.getTarget(projectId, subjectId);
            boolean isAbsent = scoreService.isStudentAbsent(projectId, studentId, target);
            if(isAbsent){
                map.put("isAbsent", isAbsent);
            }

            list.add(map);
        }

        list.sort((o1, o2) -> ((Double) o2.get("subjectScore")).compareTo(((Double) o1.get("subjectScore"))));
        return list;
    }

    // 班级知识点分析
    private List<Map<String, Object>> getClassPointAnalysis(String projectId, String subjectId, String classId) {
        Range range = Range.clazz(classId);
        return getPointStats(projectId, subjectId, range);
    }

    /**
     * 查询知识点详细数据
     * @param projectId    项目ID
     * @param subjectId    科目ID
     * @param range        范围
     * @return
     */
    public List<Map<String, Object>> getPointStats(String projectId, String subjectId, Range range) {
        List<Map<String, Object>> pointStats = new ArrayList<>();
        List<Point> points = pointService.getPoints(projectId, subjectId);
        for (Point point : points) {
            String pointId = point.getId();
            Map<String, Object> pointStat = new HashMap<>();
            pointStat.put("pointName", point.getName());

            Target target = Target.point(pointId);
            double score;

            if (range.match(Range.STUDENT)) {
                score = scoreService.getScore(projectId, range, target);
            } else {
                score = averageService.getAverage(projectId, range, target);
            }
            pointStat.put("score", DoubleUtils.round(score));

            double fullScore = fullScoreService.getFullScore(projectId, target);
            pointStat.put("fullScore", fullScore);
            pointStat.put("scoreRate", DoubleUtils.round(fullScore == 0 ? 0 : score / fullScore, true));

            pointStats.add(pointStat);
        }

        return pointStats;
    }

    public static String initSubject(String projectId, String[] authSubjectIds, SubjectService subjectService) {
        List<String> subjectIds = new ArrayList<>(subjectService.querySubjects(projectId));
        subjectIds = filterSubject(subjectIds, authSubjectIds);

        subjectIds.sort(String::compareTo);

        if (!subjectIds.isEmpty()) {
            return subjectIds.get(0);
        }

        return null;
    }
}
