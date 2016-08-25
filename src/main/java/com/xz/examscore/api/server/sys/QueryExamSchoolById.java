package com.xz.examscore.api.server.sys;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.annotation.*;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.services.SchoolService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 通过学校id查询考试学校信息
 *
 * @author zhaorenwu
 */
@Function(description = "通过学校id查询考试学校信息", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "schoolId", type = Type.String, description = "学校id", required = true)
}, result = @ResultInfo(properties = {
        @Property(name = "project", type = Type.String, description = "考试项目id"),
        @Property(name = "school", type = Type.String, description = "学校id"),
        @Property(name = "name", type = Type.String, description = "学校名称")
}))
@Service
public class QueryExamSchoolById implements Server {

    @Autowired
    SchoolService schoolService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String schoolId = param.getString("schoolId");

        Document examSchool = schoolService.findSchool(projectId, schoolId);
        if (examSchool == null) {
            return Result.fail("找不到指定的学校");
        }

        return Result.success()
                .set("project", examSchool.getString("project"))
                .set("school", examSchool.getString("school"))
                .set("name", examSchool.getString("name"));
    }
}
