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
import com.xz.services.RangeService;
import com.xz.services.ScoreSegmentService;
import com.xz.services.TargetService;
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
 * 学校成绩-分数段统计
 *
 * @author zhaorenwu
 */
@Function(description = "学校成绩-分数段统计", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "subjectId", type = Type.String, description = "科目ID", required = false),
        @Parameter(name = "schoolId", type = Type.String, description = "学校id", required = true)
})
@Service
public class SchoolScoreSegment implements Server {

    public static Logger LOG = LoggerFactory.getLogger(SchoolScoreSegment.class);

    @Autowired
    ClassService classService;

    @Autowired
    ScoreSegmentService scoreSegmentService;

    @Autowired
    TargetService targetService;

    @Autowired
    RangeService rangeService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String subjectId = param.getString("subjectId");
        String schoolId = param.getString("schoolId");

        List<Map<String, Object>> classSegments = getClassScoreSegments(projectId, subjectId, schoolId);
        List<Map<String, Object>> schoolSegments = getSchoolTotalScoreSegments(projectId, subjectId, schoolId);

        return Result.success().set("schoolSegments", schoolSegments)
                .set("classSegments", classSegments);
    }

    private List<Map<String, Object>> getClassScoreSegments(String projectId, String subjectId, String schoolId) {
        List<Map<String, Object>> schoolSegments = new ArrayList<>();

        List<Document> listClasses = classService.listClasses(projectId, schoolId);
        for (Document listClass : listClasses) {
            Map<String, Object> map = new HashMap<>();
            String classId = listClass.getString("class");
            String name = listClass.getString("name");

            Range range = Range.clazz(classId);
            Target target = targetService.getTarget(projectId, subjectId);
            List<Map<String, Object>> scoreSegments = scoreSegmentService.queryFullScoreSegment(projectId, target, range);

            map.put("scoreSegments", scoreSegments);
            map.put("className", name);
            schoolSegments.add(map);
        }

        return schoolSegments;
    }

    private List<Map<String, Object>> getSchoolTotalScoreSegments(String projectId, String subjectId, String schoolId) {
        Range range = Range.school(schoolId);
        Target target = targetService.getTarget(projectId, subjectId);

        return scoreSegmentService.queryFullScoreSegment(projectId, target, range);
    }
}
