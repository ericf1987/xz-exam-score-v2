package com.xz.examscore.api.server.project;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.AverageService;
import com.xz.examscore.services.FullScoreService;
import com.xz.examscore.services.SchoolService;
import com.xz.examscore.util.DoubleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 总体成绩-知识点对比
 *
 * @author zhaorenwu
 */
@Function(description = "总体成绩-知识点对比分析", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "pointId", type = Type.String, description = "知识点ID", required = true),
        @Parameter(name = "schoolIds", type = Type.StringArray, description = "学校id列表", required = true)
})
@Service
public class ProjectPointCompare implements Server {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectPointCompare.class);

    @Autowired
    SchoolService schoolService;

    @Autowired
    FullScoreService fullScoreService;

    @Autowired
    AverageService averageService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String pointId = param.getString("pointId");
        String[] schoolIds = param.getStringValues("schoolIds");

        List<Map<String, Object>> schools = new ArrayList<>();
        for (String schoolId : schoolIds) {
            String schoolName = schoolService.getSchoolName(projectId, schoolId);
            if (StringUtil.isBlank(schoolName)) {
                LOG.warn("找不到学校:'{}'的考试记录", schoolId);
                continue;
            }
            Map<String, Object> map = new HashMap<>();
            map.put("schoolName", schoolName);

            // 知识点得分率
            Target target = Target.point(pointId);
            double fullScore = fullScoreService.getFullScore(projectId, target);
            double average = averageService.getAverage(projectId, Range.school(schoolId), target);
            map.put("scoreRate", DoubleUtils.round(fullScore == 0 ? 0 : average / fullScore, true));
            map.put("fullScore", fullScore);

            schools.add(map);
        }

        return Result.success().set("schools", schools);
    }
}
