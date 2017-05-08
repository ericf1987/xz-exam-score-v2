package com.xz.examscore.api.server.sys;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.ajia.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.bean.AggregationConfig;
import com.xz.examscore.bean.AggregationStatus;
import com.xz.examscore.bean.AggregationType;
import com.xz.examscore.services.AggregationService;
import com.xz.examscore.services.ProjectStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.xz.examscore.bean.ProjectStatus.AggregationStarted;
import static com.xz.examscore.bean.ProjectStatus.Initializing;

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

    @Autowired
    ProjectStatusService projectStatusService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("project");

        //任务进入队列之前，先判断该考试项目是否正在统计
        AggregationStatus aggregationStatus = projectStatusService.getAggregationStatus(projectId);
        if(aggregationStatus.equals(AggregationStatus.Activated)){
            return Result.fail("该项目的统计正在执行中，不能重复执行，请稍后执行!");
        }

        //标记项目开始初始化
        projectStatusService.setAggregationStatus(projectId,  AggregationStatus.Activated);
        projectStatusService.setProjectStatus(projectId, Initializing);

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
