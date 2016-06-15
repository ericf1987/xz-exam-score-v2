package com.xz.api.server.sys;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.api.Param;
import com.xz.api.annotation.*;
import com.xz.api.server.Server;
import com.xz.bean.Range;
import com.xz.services.RangeService;
import com.xz.services.TopStudentListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 获取尖子生排名分段
 *
 * @author zhaorenwu
 */

@Function(description = "获取尖子生排名分段", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "schoolId", type = Type.String, description = "学校id", required = false)
}, result = @ResultInfo(listProperties =
@ListProperty(name = "ranksegments", description = "排名分段", properties = {
        @Property(name = "startIndex", type = Type.String, description = "开始位置"),
        @Property(name = "endIndex", type = Type.String, description = "结束位置"),
        @Property(name = "title", type = Type.String, description = "描述信息"),
})))
@Service
public class QueryTopStudentRankSegment implements Server {

    @Autowired
    TopStudentListService topStudentListService;

    @Autowired
    RangeService rangeService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String schoolId = param.getString("schoolId");

        Range range;
        if (StringUtil.isNotBlank(schoolId)) {
            range = Range.school(schoolId);
        } else {
            range = rangeService.queryProvinceRange(projectId);
        }

        List<Map<String, Object>> topStudentRankSegment =
                topStudentListService.getTopStudentRankSegment(projectId, range);
        return Result.success().set("ranksegments", topStudentRankSegment);
    }
}
