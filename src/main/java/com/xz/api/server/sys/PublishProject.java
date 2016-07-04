package com.xz.api.server.sys;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.api.annotation.Function;
import com.xz.api.annotation.Parameter;
import com.xz.api.annotation.Type;
import com.xz.api.server.Server;
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

    @Override
    public Result execute(Param param) throws Exception {
        return null;
    }
}
