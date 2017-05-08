package com.xz.examscore.api.server.sys;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.ajia.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.bean.PaperScreenShotStatus;
import com.xz.examscore.paperScreenShot.service.PaperScreenShotService;
import com.xz.examscore.services.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author by fengye on 2017/3/16.
 */
@Function(description = "执行试卷截图生成任务", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true)
})
@Service
public class StartPaperScreenShotTask implements Server{

    @Autowired
    PaperScreenShotService paperScreenShotService;

    @Autowired
    ProjectService projectService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");

        if (projectService.getPaperScreenShotStatus(projectId).equals(PaperScreenShotStatus.GENERATING)) {
            return Result.fail("该考试项目正在保存截图，请等待...");
        }

        paperScreenShotService.startPaperScreenShotTask(projectId);

        return Result.success("保存截图任务开始执行...");
    }
}
