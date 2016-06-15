package com.xz.api.server.sys;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.api.annotation.*;
import com.xz.api.server.Server;
import com.xz.bean.Range;
import com.xz.services.ReportItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 新增报表条目
 *
 * @author zhaorenwu
 */
@Function(description = "新增报表条目", parameters = {
        @Parameter(name = "name", type = Type.String, description = "报表名称"),
        @Parameter(name = "type", type = Type.String, description = "报表类型 basics=基础报表,paper=试卷分析报表,topStudent=尖子生分析"),
        @Parameter(name = "rangeName", type = Type.String, description = "报表范围名称 province=总体报表,school=学校报表,clazz=班级报表"),
        @Parameter(name = "rangeId", type = Type.String, description = "范围编号 common=通用报表,{projectId}=项目自定义报表"),
        @Parameter(name = "collectionNames", type = Type.StringArray, description = "报表数据来源的集合名称"),
        @Parameter(name = "serverName", type = Type.String, description = "报表接口名称"),
})
@Component
public class AddReportItem implements Server {

    @Autowired
    ReportItemService reportItemService;

    @Override
    public Result execute(Param param) throws Exception {
        String name = param.getString("name");
        String type = param.getString("type");
        String rangeName = param.getString("rangeName");
        String rangeId = param.getString("rangeId");
        String[] collectionNames = param.getStringValues("collectionNames");
        String serverName = param.getString("serverName");

        ReportItemService.ReportType ReportType = ReportItemService.ReportType.valueOf(type);
        ReportItemService.ReportRange reportRange = ReportItemService.ReportRange.valueOf(rangeName);

        Range range = new Range(reportRange.name(), rangeId);
        reportItemService.addReportItem(range, ReportType.name(), name, collectionNames, serverName);
        return Result.success();
    }
}
