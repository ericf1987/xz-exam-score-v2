package com.xz.examscore.api.server.customization;

        import com.mongodb.client.FindIterable;
        import com.xz.ajiaedu.common.lang.Result;
        import com.xz.examscore.api.Param;
        import com.xz.examscore.api.annotation.Function;
        import com.xz.examscore.api.annotation.Parameter;
        import com.xz.examscore.api.annotation.Type;
        import com.xz.examscore.api.server.Server;
        import com.xz.examscore.bean.Range;
        import com.xz.examscore.bean.Target;
        import com.xz.examscore.services.*;
        import org.apache.commons.collections.MapUtils;
        import org.bson.Document;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.stereotype.Service;

        import java.util.*;

/**
 * @author by fengye on 2016/10/19.
 */
@Function(description = "学生组合科目对比", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "subjectCombinationId", type = Type.String, description = "组合科目ID", required = true)
})
@Service
public class StudentSbjCbnCompare implements Server {
    @Autowired
    SubjectCombinationService subjectCombinationService;

    @Autowired
    StudentService studentService;

    @Autowired
    ClassService classService;

    @Autowired
    SchoolService schoolService;

    @Autowired
    ScoreService scoreService;

    @Autowired
    RankService rankService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String subjectCombinationId = param.getString("subjectCombinationId");
        List<Map<String, Object>> result = new ArrayList<>();
        Target subjectCombinationTarget = Target.subjectCombination(subjectCombinationId);
        FindIterable<Document> list = this.studentService.getProjectStudentList(projectId, null, -1, null);
        for (Document doc : list) {
            String studentId = doc.getString("student");
            String studentName = doc.getString("name");
            String classId = doc.getString("class");
            String className = classService.getClassName(projectId, classId);
            String schoolId = doc.getString("school");
            String examNo = doc.getString("examNo");
            double score = scoreService.getScore(projectId, Range.student(studentId), subjectCombinationTarget);
            int classRank = rankService.getRank(projectId, Range.clazz(classId), subjectCombinationTarget, score);
            int schoolRank = rankService.getRank(projectId, Range.school(schoolId), subjectCombinationTarget, score);
            Map<String, Object> studentMap = new HashMap<>();
            studentMap.put("studentName", studentName);
            studentMap.put("className", className);
            studentMap.put("examNo", examNo);
            studentMap.put("score", score);
            studentMap.put("classRank", classRank);
            studentMap.put("schoolRank", schoolRank);
            result.add(studentMap);
        }
        Collections.sort(result, (Map<String, Object> m1, Map<String, Object> m2) -> MapUtils.getDouble(m2, "score").compareTo(MapUtils.getDouble(m1, "score")));
        return new Result().set("students", result);
    }
}
