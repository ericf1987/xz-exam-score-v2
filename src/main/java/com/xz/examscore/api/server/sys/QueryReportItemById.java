package com.xz.examscore.api.server.sys;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.ajia.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.services.ReportItemService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 查询指定报表条目明细
 *
 * @author zhaorenwu
 */

@Function(description = "查询指定报表条目明细", parameters = {
        @Parameter(name = "id", type = Type.String, description = "条目id", required = true)
})
@Component
public class QueryReportItemById implements Server {

    @Autowired
    ReportItemService reportItemService;

    @Override
    public Result execute(Param param) throws Exception {
        String id = param.getString("id");

        Document reportItem = reportItemService.queryReportItemById(id);
        return Result.success().set("reportItem", reportItem);
    }
}
