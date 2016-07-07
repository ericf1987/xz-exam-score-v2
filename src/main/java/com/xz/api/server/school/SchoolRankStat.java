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

import static com.xz.api.server.sys.QueryExamClasses.getFullClassName;

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

        Map<String, Object> classRankStats = getClassRankSegments(projectId, subjectId, schoolId);
        return Result.success()
                .set("classes", classRankStats.get("classRankStats"))
                .set("hasHeader", classRankStats.get("hasHeader"));
    }

    // 学校排名分段统计
    private Map<String, Object> getClassRankSegments(String projectId, String subjectId, String schoolId) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> classRankStats = new ArrayList<>();
        boolean hasHeader = false;

        List<Document> listClasses = classService.listClasses(projectId, schoolId);
        for (Document listClass : listClasses) {
            String classId = listClass.getString("class");

            Map<String, Object> map = new HashMap<>();
            map.put("className", getFullClassName(listClass));

            // 考生人数
            Range range = Range.clazz(classId);
            Target target = targetService.getTarget(projectId, subjectId);
            map.put("studentCount",  studentService.getStudentCount(projectId, range, target));

            // 排行分段
            List<Map<String, Object>> rankStat = rankSegmentService.queryFullRankSegment(projectId, target, range);
            map.put("rankStat", rankStat);
            if (!rankStat.isEmpty()) {
                hasHeader = true;
            }

            classRankStats.add(map);
        }

        result.put("classRankStats", classRankStats);
        result.put("hasHeader", hasHeader);
        return result;
    }
}
