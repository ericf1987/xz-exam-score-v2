package com.xz.examscore.api.server.sys;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.annotation.*;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.bean.Range;
import com.xz.examscore.services.CollegeEntryLevelService;
import com.xz.examscore.services.RangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author by fengye on 2016/10/25.
 */
@Function(description = "获取本科生排名分段", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "schoolId", type = Type.String, description = "学校id", required = false)
}, result = @ResultInfo(listProperties =
@ListProperty(name = "ranksegments", description = "排名分段", properties = {
        @Property(name = "startIndex", type = Type.String, description = "开始位置"),
        @Property(name = "endIndex", type = Type.String, description = "结束位置"),
        @Property(name = "title", type = Type.String, description = "描述信息"),
})))
@Service
public class QueryELStudentRankSegment implements Server {

    @Autowired
    CollegeEntryLevelService collegeEntryLevelService;

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
                collegeEntryLevelService.getEntryLevelStudentRankSegment(projectId, range);
        return Result.success().set("ranksegments", topStudentRankSegment);
    }
}
