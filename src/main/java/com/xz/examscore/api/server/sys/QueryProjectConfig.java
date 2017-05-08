package com.xz.examscore.api.server.sys;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.ajia.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.bean.ProjectConfig;
import com.xz.examscore.intclient.InterfaceAuthClient;
import com.xz.examscore.services.ProjectConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author by fengye on 2016/9/1.
 */
@Function(description = "通过考试项目id查询项目配置参数", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true)
})
@Service
public class QueryProjectConfig implements Server{

    @Autowired
    InterfaceAuthClient interfaceAuthClient;

    @Autowired
    ProjectConfigService projectConfigService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        ProjectConfig projectConfig = projectConfigService.fixProjectConfig(new ProjectConfig(projectId));
        Result result = interfaceAuthClient.queryProjectReportConfig(projectId);
        Map<String, Object> projectConfigMap = result.getData();
        if(!projectConfigMap.containsKey("onlineRateStat")){
            Map<String, Object> onlineRateStat = new HashMap<>();
            onlineRateStat.put("values", projectConfig.getCollegeEntryLevel());
            onlineRateStat.put("isOn", String.valueOf(projectConfig.isEntryLevelEnable()));
            onlineRateStat.put("onlineStatType", projectConfig.getEntryLevelStatType());
            result.set("onlineRateStat", onlineRateStat);
        }
        return Result.success().set("projectConfig", result.getData());
    }
}
