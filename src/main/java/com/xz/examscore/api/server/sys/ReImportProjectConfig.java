package com.xz.examscore.api.server.sys;

import com.xz.ajiaedu.common.lang.Context;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.ajia.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.services.ImportProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author by fengye on 2016/11/10.
 */
@Function(description = "CMS端请求重新导入考试配置信息", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true)
})
@Service
public class ReImportProjectConfig implements Server{

    @Autowired
    ImportProjectService importProjectService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        try {
            importProjectService.importProjectReportConfig(projectId, new Context());
            return Result.success("导入完成！");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("导入失败！");
        }
    }
}
