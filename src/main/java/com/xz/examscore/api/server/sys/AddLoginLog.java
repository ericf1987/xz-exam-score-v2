package com.xz.examscore.api.server.sys;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.ajia.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.services.LoginLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 添加云报表的登陆日志
 * created at 2016/10/13.
 *
 * @author zhaorenwu
 */

@Function(description = "添加云报表的登陆日志", parameters = {
        @Parameter(name = "userId", type = Type.String, description = "用户id"),
        @Parameter(name = "userName", type = Type.String, description = "用户名称"),
        @Parameter(name = "role", type = Type.String, description = "用户角色"),
        @Parameter(name = "mobile", type = Type.String, description = "手机号码", required = false),
        @Parameter(name = "schoolId", type = Type.String, description = "学校id", required = false),
        @Parameter(name = "schoolName", type = Type.String, description = "学校名称", required = false)
})
@Service
public class AddLoginLog implements Server {

    @Autowired
    LoginLogService loginLogService;

    @Override
    public Result execute(Param param) throws Exception {
        String userId = param.getString("userId");
        String userName = param.getString("userName");
        String role = param.getString("role");
        String mobile = param.getString("mobile");
        String schoolId = param.getString("schoolId");
        String schoolName = param.getString("schoolName");

        Runnable runnable = () ->
                loginLogService.addLoginLog(userId, userName, role, mobile, schoolId, schoolName);

        new Thread(runnable).start();
        return Result.success();
    }
}
