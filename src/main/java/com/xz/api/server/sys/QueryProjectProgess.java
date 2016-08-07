package com.xz.api.server.sys;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.api.annotation.Function;
import com.xz.api.annotation.Parameter;
import com.xz.api.annotation.Type;
import com.xz.api.server.Server;
import com.xz.bean.ProjectStatus;
import com.xz.services.ProjectService;
import com.xz.util.AggregationProgressParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author by fengye on 2016/8/7.
 */
@Function(description = "查询考试项目统计流程执行进度", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true)
})
@Service
public class QueryProjectProgess implements Server{
    
    @Autowired
    ProjectService projectService;
    
    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        Map<String, String> resultMap = getProjectProgress(projectService.getProjectStatus(projectId));
        return Result.success().set("progress", resultMap);
    }

    private Map<String, String> getProjectProgress(ProjectStatus projectStatus) {
        String status = projectStatus.name();
        String progressRate = AggregationProgressParam.PROGRESS_MAP.get(status);
        Map<String, String> result = new HashMap<>();
        result.put("status", status);
        result.put("rate", progressRate);
        result.put("step", getStep(status));
        return result;
    }

    private String getStep(String status) {
        if(status.startsWith("ProjectImport")){
            return "1";
        }else if(status.startsWith("ScoreImport")){
            return "2";
        }else if (status.startsWith("Aggregation")){
            return "3";
        }else if (status.startsWith("Report")){
            return "4";
        }
        return "0";
    }

}
