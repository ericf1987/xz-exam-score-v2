package com.xz.examscore.api.server.project;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.mongo.DocumentUtils;
import com.xz.ajiaedu.common.ajia.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.*;
import org.apache.commons.lang.ArrayUtils;
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
 * 总体成绩-尖子试卷题型分析
 *
 * @author zhaorenwu
 */

@Function(description = "总体成绩-尖子生试卷题型分析", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "subjectId", type = Type.String, description = "科目ID", required = true),
        @Parameter(name = "rankSegment", type = Type.StringArray, description = "排名分段,默认为第一分数段", required = false)
})
@Service
public class ProjectTopStudentQuestTypeStat implements Server {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectTopStudentQuestTypeStat.class);

    @Autowired
    TopStudentListService topStudentListService;

    @Autowired
    SchoolService schoolService;

    @Autowired
    RangeService rangeService;

    @Autowired
    StudentService studentService;

    @Autowired
    ClassService classService;

    @Autowired
    QuestTypeScoreService questTypeScoreService;

    @Autowired
    QuestTypeService questTypeService;

    @Autowired
    FullScoreService fullScoreService;

    @Autowired
    ScoreService scoreService;

    @Autowired
    ProjectQuestTypeAnalysis projectQuestTypeAnalysis;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String subjectId = param.getString("subjectId");
        String[] rankSegment = param.getStringValues("rankSegment");

        Range range = rangeService.queryProvinceRange(projectId);
        Target target = Target.project(projectId);

        if (ArrayUtils.isEmpty(rankSegment)) {
            rankSegment = initRankSegment(projectId, range, topStudentListService);
        }

        List<Map<String, Object>> totalQuestTypeAnalysis = projectQuestTypeAnalysis.getQuestTypeAnalysis(projectId, subjectId, range);

        List<Map<String, Object>> topStudents = getTopStudentQuestTypeStat(projectId, rankSegment, range, target, subjectId);

        return Result.success()
                .set("totals", totalQuestTypeAnalysis)
                .set("topStudents", topStudents)
                .set("hasHeader", !totalQuestTypeAnalysis.isEmpty());
    }

    // 初始化排名分数段
    public static String[] initRankSegment(String projectId, Range range, TopStudentListService topStudentListService) {
        List<Map<String, Object>> topStudentRankSegment = topStudentListService.getTopStudentRankSegment(projectId, range);
        if (topStudentRankSegment.isEmpty()) {
            return new String[]{"0", "0"};
        } else {
            Map<String, Object> rankSegmentMap = topStudentRankSegment.get(0);
            return new String[]{String.valueOf(rankSegmentMap.get("startIndex")),
                    String.valueOf(rankSegmentMap.get("endIndex"))};
        }
    }

    // 尖子生题型统计
    public List<Map<String, Object>> getTopStudentQuestTypeStat(
            String projectId, String[] rankSegment, Range range, Target target, String subjectId) {

        List<Map<String, Object>> topStudents = new ArrayList<>();
        int minIndex = NumberUtils.toInt(rankSegment[0]);
        int maxIndex = NumberUtils.toInt(rankSegment[1]);

        List<Document> topStudentList =
                topStudentListService.getTopStudentList(projectId, range, target, minIndex, maxIndex);
        for (Document document : topStudentList) {
            Map<String, Object> map = new HashMap<>();
            String studentId = document.getString("student");
            double totalScore = DocumentUtils.getDouble(document, "score", 0);
            int rank = DocumentUtils.getInt(document, "rank", 0);

            Document student = studentService.findStudent(projectId, studentId);
            if (student == null) {
                LOG.warn("找不到学生'" + studentId + "'的考试'" + projectId + "'记录");
                continue;
            }

            double subjectScore = scoreService.getSubjectScore(projectId, studentId, subjectId);
            //学生本次考试考号
            map.put("examNo", student.getString("examNo"));
            map.put("customExamNo", student.getString("customExamNo"));
            map.put("name", student.getString("name"));
            map.put("rank", rank);
            map.put("totalScore", totalScore);
            map.put("subjectScore", subjectScore);

            if (range.match(Range.SCHOOL)) {
                String classId = student.getString("class");
                map.put("classId", classId);
                map.put("className", classService.getClassName(projectId, classId));
            } else {
                String schoolId = student.getString("school");
                map.put("schoolId", schoolId);
                map.put("schoolName", schoolService.getSchoolName(projectId, schoolId));
            }

            Range _range = Range.student(studentId);
            List<Map<String, Object>> questTypeAnalysis = projectQuestTypeAnalysis.getQuestTypeAnalysis(projectId, subjectId, _range);
            map.put("questTypes", questTypeAnalysis);

            topStudents.add(map);
        }

        topStudents.sort((o1, o2) -> ((Double) o2.get("totalScore")).compareTo((Double) o1.get("totalScore")));
        return topStudents;
    }
}
