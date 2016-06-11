package com.xz.api.server.project;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.api.Param;
import com.xz.api.annotation.Function;
import com.xz.api.annotation.Parameter;
import com.xz.api.annotation.Type;
import com.xz.api.server.Server;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.services.RankSegmentService;
import com.xz.services.SchoolService;
import com.xz.services.StudentService;
import com.xz.services.TargetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 总体成绩-排名统计
 *
 * @author zhaorenwu
 */
@Function(description = "总体成绩-排名统计", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "subjectId", type = Type.String, description = "科目ID", required = false),
        @Parameter(name = "schoolIds", type = Type.StringArray, description = "学校id列表", required = true)
})
@Service
public class ProjectRankStat implements Server {

    private static Logger LOG = LoggerFactory.getLogger(ProjectRankStat.class);

    @Autowired
    RankSegmentService rankSegmentService;

    @Autowired
    SchoolService schoolService;

    @Autowired
    StudentService studentService;

    @Autowired
    TargetService targetService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String subjectId = param.getString("subjectId");
        String[] schoolIds = param.getStringValues("schoolIds");

        List<Map<String, Object>> schoolRankSegments = getSchoolRankSegments(projectId, subjectId, schoolIds);
        return Result.success().set("schools", schoolRankSegments);
    }

    // 学校排名分段统计
    private List<Map<String, Object>> getSchoolRankSegments(String projectId, String subjectId, String[] schoolIds) {
        List<Map<String, Object>> schoolRankSegments = new ArrayList<>();

        for (String schoolId : schoolIds) {
            String schoolName = schoolService.getSchoolName(projectId, schoolId);
            if (StringUtil.isBlank(schoolName)) {
                LOG.warn("找不到学校:'{}'的考试记录", schoolId);
                continue;
            }
            Map<String, Object> map = new HashMap<>();
            map.put("schoolName", schoolName);

            // 考生人数
            Range range = Range.school(schoolId);
            Target target = targetService.getTarget(projectId, subjectId);
            map.put("studentCount",  studentService.getStudentCount(projectId, range, target));

            // 排行分段
            List<Map<String, Object>> rankStat = rankSegmentService.queryFullRankSegment(projectId, target, range);
            map.put("rankStat", rankStat);

            schoolRankSegments.add(map);
        }

        return schoolRankSegments;
    }
}
