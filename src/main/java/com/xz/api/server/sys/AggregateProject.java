package com.xz.api.server.sys;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.api.annotation.Function;
import com.xz.api.annotation.Parameter;
import com.xz.api.annotation.Type;
import com.xz.api.server.Server;
import org.springframework.stereotype.Component;

/**
 * @author by fengye on 2016/8/7.
 */
@Function(description = "调用数据统计接口", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试ID"),
        @Parameter(name = "reportType", type = Type.String, description = "生成报表类型", required = false),
        @Parameter(name = "reimportProject", type = Type.String, description = "是否重新导入考试项目", required = false)
})
@Component
public class AggregateProject implements Server{
    @Override
    public Result execute(Param param) throws Exception {
        return null;
    }
}
