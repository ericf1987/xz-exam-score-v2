package com.xz.api.server.project;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.api.annotation.Function;
import com.xz.api.annotation.Parameter;
import com.xz.api.annotation.Type;
import com.xz.api.server.Server;
import com.xz.services.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 总体成绩-尖子生统计
 *
 * @author zhaorenwu
 */

@Function(description = "总体成绩-尖子生统计", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "schoolIds", type = Type.StringArray, description = "学校id列表", required = true),
        @Parameter(name = "rankSegment", type = Type.StringArray, description = "排名分段", required = true)
})
@Service
public class ProjectTopStudentStat implements Server {

    @Autowired
    SubjectService subjectService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String[] schoolIds = param.getStringValues("schoolIds");
        String[] rankSegment = param.getStringValues("rankSegment");

        return Result.success();
    }
}
