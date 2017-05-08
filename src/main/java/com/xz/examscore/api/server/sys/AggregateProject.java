package com.xz.examscore.api.server.sys;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.ajia.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
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
