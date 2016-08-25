package com.xz.examscore.api.server.sys;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.annotation.*;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.services.ProjectService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 查询学校考试项目列表
 *
 * @author zhaorenwu
 */

@Function(description = "查询学校考试项目列表", parameters = {
        @Parameter(name = "schoolId", type = Type.String, description = "学校id", required = true),
        @Parameter(name = "month", type = Type.String, description = "月份 格式 yyyy-MM", required = false)
}, result = @ResultInfo(listProperties =
@ListProperty(name = "projects", description = "考试项目列表", properties = {
        @Property(name = "project", type = Type.String, description = "考试项目id"),
        @Property(name = "name", type = Type.String, description = "考试项目名称"),
        @Property(name = "importDate", type = Type.String, description = "记录创建时间"),
        @Property(name = "grade", type = Type.String, description = "年级")
})))
@Component
public class QuerySchoolProjects implements Server {

    @Autowired
    ProjectService projectService;

    @Override
    public Result execute(Param param) throws Exception {
        String schoolId = param.getString("schoolId");
        String month = param.getString("month");

        List<Document> projects = projectService.querySchoolProjects(schoolId, month);
        return Result.success().set("projects", projects);
    }
}
