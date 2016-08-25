package com.xz.examscore.api.server.sys;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.services.ProjectStatusService;
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
        return Result.success().set("projectStatus", projectStatusService.getProjectStatus(projectId));
    }
}
