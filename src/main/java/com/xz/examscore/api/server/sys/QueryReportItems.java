package com.xz.examscore.api.server.sys;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.services.ReportItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 查询指定项目的报表条目列表
 *
 * @author zhaorenwu
 */

@Function(description = "查询指定项目的报表条目列表", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目id", required = true),
        @Parameter(name = "schoolId", type = Type.String, description = "学校id", required = false),
        @Parameter(name = "isEduBureauAccount", type = Type.String, description = "是否是教育局账号", required = true)
})
@Component
public class QueryReportItems implements Server {

    @Autowired
    ReportItemService reportItemService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String isEduBureauAccount = param.getString("isEduBureauAccount");
        Result result = Result.success();
        result.setData(reportItemService.querySchoolReportItems(projectId, isEduBureauAccount));
        return result;
    }
}
