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
import com.xz.services.TopStudentListService;
import com.xz.util.DoubleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 总体成绩-尖子生对比分析
 *
 * @author zhaorenwu
 */
@Function(description = "总体成绩-尖子生对比分析", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "schoolIds", type = Type.StringArray, description = "学校id列表", required = true)
})
@Service
public class ProjectTopStudentCompare implements Server {

    private static Logger LOG = LoggerFactory.getLogger(ProjectTopStudentCompare.class);

    @Autowired
    SchoolService schoolService;

    @Autowired
    RangeService rangeService;

    @Autowired
    TopStudentListService topStudentListService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String[] schoolIds = param.getStringValues("schoolIds");

        List<Map<String, Object>> schoolTopStudentRates =  getSchoolTopStudentRates(projectId, schoolIds);
        return Result.success().set("schools", schoolTopStudentRates);
    }

    // 学校在项目中的尖子生比率
    private List<Map<String, Object>> getSchoolTopStudentRates(String projectId, String[] schoolIds) {
        List<Map<String, Object>> list = new ArrayList<>();

        for (String schoolId : schoolIds) {
            String schoolName = schoolService.getSchoolName(projectId, schoolId);
            if (StringUtil.isBlank(schoolName)) {
                LOG.warn("找不到学校:'{}'的考试记录", schoolId);
                continue;
            }

            Map<String, Object> map = new HashMap<>();
            map.put("schoolName", schoolName);

            Range rankRange = rangeService.queryProvinceRange(projectId);
            Range compareRange = Range.school(schoolId);
            Target target = Target.project(projectId);
            int topStudentTotalCount = topStudentListService.getTopStudentTotalCount(projectId, rankRange);
            int topStudentCount = topStudentListService.getTopStudentCount(projectId, rankRange,
                    compareRange, target, 1, topStudentTotalCount);

            map.put("count", topStudentCount);
            map.put("rate", DoubleUtils.round(topStudentTotalCount == 0 ?
                    0 : topStudentCount * 1.0 / topStudentTotalCount, true));

            list.add(map);
        }

        return list;
    }
}
