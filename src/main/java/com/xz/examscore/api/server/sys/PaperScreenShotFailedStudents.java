package com.xz.examscore.api.server.sys;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.ajia.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.paperScreenShot.service.MonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author by fengye on 2017/3/26.
 */
@Function(description = "查看试卷截图生成失败的学生列表", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "classId", type = Type.String, description = "班级ID", required = true),
        @Parameter(name = "subjectId", type = Type.String, description = "科目ID", required = true)
})
@Service
public class PaperScreenShotFailedStudents implements Server{

    @Autowired
    MonitorService monitorService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String classId = param.getString("classId");
        String subjectId = param.getString("subjectId");

        List<String> failedStudents = monitorService.getFailedStudents(projectId, classId, subjectId);

        return Result.success().set("students", failedStudents);
    }
}
