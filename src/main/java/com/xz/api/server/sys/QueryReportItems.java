package com.xz.api.server.sys;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.api.annotation.Function;
import com.xz.api.annotation.Parameter;
import com.xz.api.annotation.Type;
import com.xz.api.server.Server;
import com.xz.services.ReportItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 查询指定项目的报表条目列表
 *
 * @author zhaorenwu
 */

@Function(description = "查询指定项目的报表条目列表", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目id", required = true),
        @Parameter(name = "schoolId", type = Type.String, description = "学校id", required = false)
})
@Component
public class QueryReportItems implements Server {

    @Autowired
    ReportItemService reportItemService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");

        Result result = Result.success();
        result.setData(reportItemService.querySchoolReportItems(projectId));
        return result;
    }
}
