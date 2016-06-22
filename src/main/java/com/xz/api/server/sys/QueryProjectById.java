package com.xz.api.server.sys;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.api.annotation.*;
import com.xz.api.server.Server;
import com.xz.services.ProjectService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 通过考试项目id查询项目信息
 *
 * @author zhaorenwu
 */
@Function(description = "通过考试项目id查询项目信息", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true)
}, result = @ResultInfo(properties = {
        @Property(name = "project", type = Type.String, description = "考试项目id"),
        @Property(name = "name", type = Type.String, description = "项目名称"),
        @Property(name = "importDate", type = Type.String, description = "创建时间"),
        @Property(name = "grade", type = Type.Integer, description = "年级")
}))
@Service
public class QueryProjectById implements Server {

    @Autowired
    ProjectService projectService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");

        Document document = projectService.findProject(projectId);
        if (document == null) {
            return Result.fail("找不到指定的考试项目");
        }

        Result result = Result.success();
        result.setData(document);
        return result;
    }
}
