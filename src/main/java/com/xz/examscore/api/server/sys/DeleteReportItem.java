package com.xz.examscore.api.server.sys;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.ajia.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.services.ReportItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 删除报表条目
 *
 * @author zhaorenwu
 */

@Function(description = "删除报表条目", parameters = {
        @Parameter(name = "id", type = Type.String, description = "报表条目id")
})
@Component
public class DeleteReportItem implements Server {

    @Autowired
    ReportItemService reportItemService;

    @Override
    public Result execute(Param param) throws Exception {
        String id = param.getString("id");

        reportItemService.deleteReportItem(id);
        return Result.success();
    }
}
