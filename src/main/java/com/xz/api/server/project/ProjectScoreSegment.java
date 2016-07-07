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
import com.xz.services.RangeService;
import com.xz.services.SchoolService;
import com.xz.services.ScoreSegmentService;
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
 * 总体成绩-分数段统计
 *
 * @author zhaorenwu
 */
@Function(description = "总体成绩-分数段统计", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "subjectId", type = Type.String, description = "科目ID", required = false),
        @Parameter(name = "schoolIds", type = Type.StringArray, description = "学校id列表", required = true)
})
@Service
public class ProjectScoreSegment implements Server {

    public static Logger LOG = LoggerFactory.getLogger(ProjectScoreSegment.class);

    @Autowired
    SchoolService schoolService;

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
        String[] schoolIds = param.getStringValues("schoolIds");

        List<Map<String, Object>> schoolSegments = getSchoolScoreSegments(projectId, subjectId, schoolIds);
        List<Map<String, Object>> totalSegments = getTotalScoreSegments(projectId, subjectId);

        return Result.success()
                .set("schools", schoolSegments)
                .set("totals", totalSegments)
                .set("hasHeader", !totalSegments.isEmpty());
    }

    private List<Map<String, Object>> getSchoolScoreSegments(String projectId, String subjectId, String[] schoolIds) {
        List<Map<String, Object>> schoolSegments = new ArrayList<>();

        for (String schoolId : schoolIds) {
            String schoolName = schoolService.getSchoolName(projectId, schoolId);
            if (StringUtil.isBlank(schoolName)) {
                LOG.warn("找不到学校:'{}'的考试记录", schoolId);
                continue;
            }
            Map<String, Object> map = new HashMap<>();
            map.put("schoolName", schoolName);

            Range range = Range.school(schoolId);
            Target target = targetService.getTarget(projectId, subjectId);
            List<Map<String, Object>> scoreSegments = scoreSegmentService.queryFullScoreSegment(projectId, target, range);
            map.put("scoreSegments", scoreSegments);

            schoolSegments.add(map);
        }

        return schoolSegments;
    }

    private List<Map<String, Object>> getTotalScoreSegments(String projectId, String subjectId) {
        Range range = rangeService.queryProvinceRange(projectId);
        Target target = targetService.getTarget(projectId, subjectId);

        return scoreSegmentService.queryFullScoreSegment(projectId, target, range);
    }
}
