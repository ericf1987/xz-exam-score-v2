package com.xz.api.server.school;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.api.annotation.Function;
import com.xz.api.annotation.Parameter;
import com.xz.api.annotation.Type;
import com.xz.api.server.Server;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.services.ClassService;
import com.xz.services.RankSegmentService;
import com.xz.services.StudentService;
import com.xz.services.TargetService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 学校成绩-排名统计
 *
 * @author zhaorenwu
 */
@Function(description = "学校成绩-排名统计", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "subjectId", type = Type.String, description = "科目ID", required = false),
        @Parameter(name = "schoolId", type = Type.String, description = "学校id", required = true)
})
@Service
public class SchoolRankStat implements Server {

    @Autowired
    ClassService classService;

    @Autowired
    TargetService targetService;

    @Autowired
    StudentService studentService;

    @Autowired
    RankSegmentService rankSegmentService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String subjectId = param.getString("subjectId");
        String schoolId = param.getString("schoolId");

        List<Map<String, Object>> classRankStats = getClassRankSegments(projectId, subjectId, schoolId);
        return Result.success().set("classes", classRankStats);
    }

    // 学校排名分段统计
    private List<Map<String, Object>> getClassRankSegments(String projectId, String subjectId, String schoolId) {
        List<Map<String, Object>> classRankStats = new ArrayList<>();

        List<Document> listClasses = classService.listClasses(projectId, schoolId);
        for (Document listClass : listClasses) {
            String classId = listClass.getString("class");
            String name = listClass.getString("name");

            Map<String, Object> map = new HashMap<>();
            map.put("className", name);

            // 考生人数
            Range range = Range.clazz(classId);
            Target target = targetService.getTarget(projectId, subjectId);
            map.put("studentCount",  studentService.getStudentCount(projectId, range, target));

            // 排行分段
            List<Map<String, Object>> rankStat = rankSegmentService.queryFullRankSegment(projectId, target, range);
            map.put("rankStat", rankStat);

            classRankStats.add(map);
        }

        return classRankStats;
    }
}
