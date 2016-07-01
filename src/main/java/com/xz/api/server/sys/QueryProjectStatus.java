package com.xz.api.server.sys;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.api.annotation.Function;
import com.xz.api.annotation.Parameter;
import com.xz.api.annotation.Type;
import com.xz.api.server.Server;
import com.xz.bean.ProjectStatus;
import com.xz.services.ProjectStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 通过考试项目id查询项目报表统计状态
 *
 * @author zhaorenwu
 */

@Function(description = "通过考试项目id查询项目报表统计状态", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true)
})
@Service
public class QueryProjectStatus implements Server {

    @Autowired
    ProjectStatusService projectStatusService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");

        ProjectStatus projectStatus = projectStatusService.getProjectStatus(projectId);
        return Result.success().set("projectStatus", projectStatus);
    }
}
