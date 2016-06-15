package com.xz.api.server.sys;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.api.Param;
import com.xz.api.annotation.Function;
import com.xz.api.annotation.Parameter;
import com.xz.api.annotation.Type;
import com.xz.api.server.Server;
import com.xz.services.ReportItemService;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 更新报表条目
 *
 * @author zhaorenwu
 */

@Function(description = "新增报表条目", parameters = {
        @Parameter(name = "id", type = Type.String, description = "报表条目id"),
        @Parameter(name = "type", type = Type.String, description = "报表类型 basics=基础报表,paper=试卷分析报表,topStudent=尖子生分析", required = false),
        @Parameter(name = "name", type = Type.String, description = "报表名称", required = false),
        @Parameter(name = "collectionNames", type = Type.StringArray, description = "报表数据来源的集合名称", required = false),
        @Parameter(name = "serverName", type = Type.String, description = "报表接口名称", required = false),
        @Parameter(name = "position", type = Type.String, description = "位置", required = false)
})
@Component
public class UpdateReportItem implements Server {

    @Autowired
    ReportItemService reportItemService;

    @Override
    public Result execute(Param param) throws Exception {
        String id = param.getString("id");
        String type = param.getString("type");
        String name = param.getString("name");
        String[] collectionNames = param.getStringValues("collectionNames");
        String serverName = param.getString("serverName");
        String position = param.getString("position");

        if (StringUtil.isNotBlank(type)) {
            ReportItemService.ReportType reportType = ReportItemService.ReportType.valueOf(type);
            type = reportType.name();
        }

        reportItemService.updateReportItem(id, type, name, collectionNames, serverName, position);
        return Result.success();
    }
}
