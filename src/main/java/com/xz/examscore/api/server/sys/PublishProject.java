package com.xz.examscore.api.server.sys;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.intclient.InterfaceAuthClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * (description)
 * created at 16/07/01
 *
 * @author yiding_he
 */
@Function(description = "发布项目成绩", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "项目ID", required = true)
})
@Service
public class PublishProject implements Server {

    @Autowired
    InterfaceAuthClient interfaceAuthClient;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        interfaceAuthClient.releaseExamScore(projectId);
        return Result.success();
    }
}
