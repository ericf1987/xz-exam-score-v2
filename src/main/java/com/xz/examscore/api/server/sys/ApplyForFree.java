package com.xz.examscore.api.server.sys;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.intclient.InterfaceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author by fengye on 2016/8/3.
 */
@Function(description = "申请免费启用服务", parameters = {
        @Parameter(name = "schoolName", type = Type.String, description = "申请学校名称"),
        @Parameter(name = "applicantName", type = Type.String, description = "申请人"),
        @Parameter(name = "mobile", type = Type.String, description = "申请人联系方式"),
})
@Service
public class ApplyForFree implements Server{

    @Autowired
    InterfaceClient interfaceClient;

    @Override
    public Result execute(Param param) throws Exception {
        interfaceClient.addRpApplyOpen(param);
        return Result.success();
    }
}
