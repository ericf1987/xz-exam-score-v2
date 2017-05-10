package com.xz.examscore.api.server.sys;

import com.xz.ajiaedu.common.ajia.Param;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.bean.PaperScreenShotStatus;
import com.xz.examscore.services.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author by fengye on 2017/5/8.
 */
@Function(description = "查看试卷留痕进度", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true)
})
@Service
public class QueryPaperScreenShotStatus implements Server{

    @Autowired
    ProjectService projectService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        PaperScreenShotStatus paperScreenShotStatus = projectService.getPaperScreenShotStatus(projectId);
        return Result.success().set("paperScreenShotStatus", paperScreenShotStatus.name());
    }
}
