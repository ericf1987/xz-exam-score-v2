package com.xz.examscore.api.server.sys;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.ajia.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.paperScreenShot.service.DownloadScreenShotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author by fengye on 2017/3/6.
 */
@Function(description = "打包下载学生试卷留痕", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "项目ID", required = true),
        @Parameter(name = "schoolId", type = Type.String, description = "学校ID", required = true),
        @Parameter(name = "classIds", type = Type.StringArray, description = "班级列表", required = true)
})
@Component
public class DownloadPaperScreenShot implements Server{

    @Autowired
    DownloadScreenShotService downloadScreenShotService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String schoolId = param.getString("schoolId");
        String[] classIds = param.getStringValues("classIds");
        Map<String, Object> map = downloadScreenShotService.downloadGeneratedPaperScreenShot(projectId, schoolId, classIds);
        return Result.success().set("downloadInfo", map);
    }
}
