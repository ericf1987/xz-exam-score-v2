package com.xz.examscore.api.server.sys;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.bean.AggregationConfig;
import com.xz.examscore.bean.AggregationType;
import com.xz.examscore.services.AggregationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author by fengye on 2016/9/21.
 */
@Function(description = "统计调用接口", parameters = {
        @Parameter(name = "project", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "type", type = Type.String, description = "统计类型", required = true),
        @Parameter(name = "reimportProject", type = Type.String, description = "是否要重新导入项目信息", required = false),
        @Parameter(name = "reimportScore", type = Type.String, description = "是否要重新导入和计算成绩", required = false),
        @Parameter(name = "generateReport", type = Type.String, description = "是否要生成", required = false),
        @Parameter(name = "exportScore", type = Type.String, description = "是否要将成绩导出到阿里云", required = false)

})
@Service
public class StartAggregation implements Server{
    @Autowired
    AggregationService aggregationService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("project");
        if (aggregationService.isAggregationRunning(projectId)) {
            return Result.fail("项目 " + projectId + " 正在统计当中");
        }

        AggregationConfig aggregationConfig = new AggregationConfig();
        aggregationConfig.setAggregationType(AggregationType.valueOf(param.getString("type")));
        aggregationConfig.setReimportProject(Boolean.valueOf(param.getString("reimportProject")));
        aggregationConfig.setReimportScore(Boolean.valueOf(param.getString("reimportScore")));
        aggregationConfig.setGenerateReport(Boolean.valueOf(param.getString("generateReport")));
        aggregationConfig.setExportScore(Boolean.valueOf(param.getString("exportScore")));

        aggregationService.startAggregation(projectId, aggregationConfig);

        return Result.success("项目 " + projectId + " 已经开始统计。");
    }
}
