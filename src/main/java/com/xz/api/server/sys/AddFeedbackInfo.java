package com.xz.api.server.sys;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.api.annotation.Function;
import com.xz.api.annotation.Parameter;
import com.xz.api.annotation.Type;
import com.xz.api.server.Server;
import com.xz.intclient.InterfaceClient;
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
    InterfaceClient interfaceClient;

    @Override
    public Result execute(Param param) throws Exception {
        return interfaceClient.request("AddRpFeedbackInfo", param);
    }
}
