package com.xz.examscore.api.server.project;

import com.xz.ajiaedu.common.lang.CollectionUtils;
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
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.xz.examscore.services.SubjectService.getSubjectName;

/**
 * 总体成绩-尖子生统计
 *
 * @author zhaorenwu
 */

@Function(description = "总体成绩-尖子生统计", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "rankSegment", type = Type.StringArray, description = "排名分段", required = true),
        @Parameter(name = "authSubjectIds", type = Type.StringArray, description = "可访问科目范围，为空返回所有", required = false)
})
@Service
public class ProjectTopStudentStat implements Server {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectTopStudentStat.class);

    @Autowired
    SubjectService subjectService;

    @Autowired
    TopStudentListService topStudentListService;

    @Autowired
    SchoolService schoolService;

    @Autowired
    RangeService rangeService;

    @Autowired
    StudentService studentService;

    @Autowired
    ScoreService scoreService;

    @Autowired
    RankService rankService;

    @Autowired
    ClassService classService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String[] rankSegment = param.getStringValues("rankSegment");
        String[] authSubjectIds = param.getStringValues("authSubjectIds");

        Range range = rangeService.queryProvinceRange(projectId);
        Target target = Target.project(projectId);
        List<String> subjectIds = new ArrayList<>(subjectService.querySubjects(projectId));
        subjectIds = filterSubject(subjectIds, authSubjectIds);
        subjectIds.sort(String::compareTo);

        List<Map<String, Object>> topStudents = getTopStudents(projectId, rankSegment, range, target, subjectIds);
        return Result.success().set("topStudents", topStudents).set("hasHeader", !topStudents.isEmpty());
    }

    // 尖子生查询
    public List<Map<String, Object>> getTopStudents(
            String projectId, String[] rankSegment, Range range, Target target, List<String> subjectIds) {

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

            String schoolId = student.getString("school");
            String classId = student.getString("class");
            //学生本次考试考号
            map.put("examNo", student.getString("examNo"));
            map.put("name", student.getString("name"));
            map.put("totalScore", totalScore);

            if (range.match(Range.SCHOOL)) {
                map.put("classId", classId);
                map.put("className", classService.getClassName(projectId, classId));
            } else {
                map.put("schoolId", schoolId);
                map.put("schoolName", schoolService.getSchoolName(projectId, schoolId));
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
            subjectStat(projectId, subjectIds, studentId, list, range);

            map.put("subjects", list);
            topStudents.add(map);
        }

        topStudents.sort((o1, o2) -> ((Double) o2.get("totalScore")).compareTo((Double) o1.get("totalScore")));
        return topStudents;
    }

    public void subjectStat(String projectId, List<String> subjectIds,
                                    String studentId, List<Map<String, Object>> list, Range range) {
        // 各科分析
        for (String subjectId : subjectIds) {
            Map<String, Object> subjectInfo = new HashMap<>();

            // 科目得分
            Range _range = Range.student(studentId);
            Target _target = Target.subject(subjectId);
            double score = scoreService.getScore(projectId, _range, _target);
            subjectInfo.put("score", score);

            // 科目排名
            //int rankIndex = rankService.getRank(projectId, _range, _target, score);
            // 获取学生在当前range下的科目排名
            int rankIndex = rankService.getRank(projectId, range, _target, studentId);
            subjectInfo.put("rankIndex", rankIndex);

            subjectInfo.put("subjectId", subjectId);
            subjectInfo.put("subjectName", getSubjectName(subjectId));
            list.add(subjectInfo);
        }
    }

    public static List<String> filterSubject(List<String> subjectIds, String[] authSubjects) {

        if (ArrayUtils.isEmpty(authSubjects) || CollectionUtils.isEmpty(subjectIds)) {
            return subjectIds;
        }

        Iterator<String> iterator = subjectIds.iterator();
        while (iterator.hasNext()) {
            String subjectId = iterator.next();

            if (!Arrays.asList(authSubjects).contains(subjectId)) {
                iterator.remove();
            }
        }

        return subjectIds;
    }
}
