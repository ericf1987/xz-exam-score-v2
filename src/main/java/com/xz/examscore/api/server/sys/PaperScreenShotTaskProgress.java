package com.xz.examscore.api.server.sys;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.ajia.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.paperScreenShot.bean.TaskProcess;
import com.xz.examscore.paperScreenShot.service.MonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author by fengye on 2017/3/26.
 */
@Function(description = "查看试卷留痕进度", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "taskProcess", type = Type.String, description = "试卷留痕任务进度", required = true)
})
@Service
public class PaperScreenShotTaskProgress implements Server{

    @Autowired
    MonitorService monitorService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String taskProcess = param.getString("taskProcess");

        double finishRate = monitorService.getFinishRate(projectId, TaskProcess.valueOf(taskProcess));

        return Result.success().set("finishRate", finishRate);
    }
}
