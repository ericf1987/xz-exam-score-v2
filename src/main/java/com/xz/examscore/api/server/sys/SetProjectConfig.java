package com.xz.examscore.api.server.sys;

import com.xz.ajiaedu.common.aliyun.ApiResponse;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.bean.ProjectConfig;
import com.xz.examscore.intclient.InterfaceClient;
import com.xz.examscore.services.ProjectConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author by fengye on 2016/9/1.
 */
@Function(description = "提交项目配置参数，并发送至阿里云", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true)
})
@Service
public class SetProjectConfig implements Server{

    @Autowired
    ProjectConfigService projectConfigService;

    @Autowired
    InterfaceClient interfaceClient;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        ProjectConfig projectConfig = convert(param);
        String projectConfigJson = convert1(param);
        Param _param = new Param().setParameter("projectId", projectId)
                .setParameter("settings", projectConfigJson);
        ApiResponse apiResponse = interfaceClient.setProjectConfig(_param);
        if(apiResponse.isSuccess()){
            try {
                projectConfigService.updateRankLevelConfig(projectConfig);
                return Result.success("配置保存成功!");
            } catch (Exception e) {
                return Result.fail("配置保存失败!");
            }
        }else{
            return Result.fail("配置保存失败!");
        }
    }

    //将参数转化为json
    private String convert1(Param param) {
        return null;
    }

    //将参数转化为ProjectConfig对象
    private ProjectConfig convert(Param param) {
        return null;
    }
}
