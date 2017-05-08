package com.xz.examscore.api.server.sys;

import com.xz.ajiaedu.common.ajia.Param;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.intclient.InterfaceAuthClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 添加报表反馈信息
 *
 * @author zhaorenwu
 */

@Function(description = "添加报表反馈信息", parameters = {
        @Parameter(name = "userId", type = Type.String, description = "用户id", required = true),
        @Parameter(name = "mobile", type = Type.String, description = "用户手机号码"),
        @Parameter(name = "name", type = Type.String, description = "用户姓名", required = false),
        @Parameter(name = "schoolId", type = Type.String, description = "用户所属学校", required = false),
        @Parameter(name = "schoolName", type = Type.String, description = "用户所属学校名称", required = false),
        @Parameter(name = "projectId", type = Type.String, description = "考试项目id", required = false),
        @Parameter(name = "projectName", type = Type.String, description = "考试项目名称", required = false),
        @Parameter(name = "message", type = Type.String, description = "反馈消息", required = true)
})
@Service
public class AddFeedbackInfo implements Server {

    @Autowired
    InterfaceAuthClient interfaceAuthClient;

    @Override
    public Result execute(Param param) throws Exception {
        interfaceAuthClient.addRpFeedbackInfo(param);
        return Result.success();
    }
}
