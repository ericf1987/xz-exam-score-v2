package com.xz.examscore.api.server.project;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.ajiaedu.common.ajia.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.RankSegmentService;
import com.xz.examscore.services.SchoolService;
import com.xz.examscore.services.StudentService;
import com.xz.examscore.services.TargetService;
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

        Target target = targetService.getTarget(projectId, subjectId);
        Map<String, Object> schoolRankSegments = getSchoolRankSegments(projectId, target, schoolIds);
        return Result.success()
                .set("schools", schoolRankSegments.get("schoolRankSegments"))
                .set("hasHeader", schoolRankSegments.get("hasHeader"));
    }

    // 学校排名分段统计
    private Map<String, Object> getSchoolRankSegments(String projectId, Target target, String[] schoolIds) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> schoolRankSegments = new ArrayList<>();

        boolean hasHeader = false;
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

            map.put("studentCount",  studentService.getStudentCount(projectId, range, target));

            // 排行分段
            List<Map<String, Object>> rankStat = rankSegmentService.queryFullRankSegment(projectId, target, range);
            map.put("rankStat", rankStat);
            if (!rankStat.isEmpty()) {
                hasHeader = true;
            }

            schoolRankSegments.add(map);
        }

        result.put("schoolRankSegments", schoolRankSegments);
        result.put("hasHeader", hasHeader);
        return result;
    }
}
