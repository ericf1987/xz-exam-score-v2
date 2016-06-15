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
