package com.xz.api.server.school;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.api.annotation.Function;
import com.xz.api.annotation.Parameter;
import com.xz.api.annotation.Type;
import com.xz.api.server.Server;
import org.springframework.stereotype.Service;

/**
 * 学校成绩-尖子生统计
 *
 * @author zhaorenwu
 */

@Function(description = "学校成绩-尖子生统计", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "schoolId", type = Type.String, description = "学校id", required = true),
        @Parameter(name = "rankSegment", type = Type.StringArray, description = "排名分段", required = true)
})
@Service
public class SchoolTopStudentStat implements Server {

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String schoolId = param.getString("schoolId");
        String[] rankSegment = param.getStringValues("rankSegment");

        return Result.success();
    }
}
