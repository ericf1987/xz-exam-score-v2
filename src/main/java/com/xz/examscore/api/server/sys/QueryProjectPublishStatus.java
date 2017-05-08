package com.xz.examscore.api.server.sys;

import com.alibaba.fastjson.JSONObject;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.ajia.Param;
import com.xz.examscore.api.annotation.*;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.intclient.InterfaceAuthClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 查询考试项目发布状态
 *
 * @author zhaorenwu
 */
@Function(description = "查询考试项目发布状态", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "项目ID")
}, result = @ResultInfo(properties = {
        @Property(name = "scoreStatus", type = Type.String,
                description = "项目发布状态 0=未发布成绩 1=已发布成绩且当前成绩数据为最新数据 2=已发布成绩且当前成绩数据有更新，需要重新发布")
}))
@Service
public class QueryProjectPublishStatus implements Server {

    @Autowired
    InterfaceAuthClient interfaceAuthClient;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");

        JSONObject projectObj = interfaceAuthClient.queryProjectById(projectId);
        if (projectObj == null) {
            return Result.fail("项目结果属性为空");
        }

        int scoreStatus = projectObj.getInteger("scoreStatus");
        return Result.success().set("scoreStatus", scoreStatus);
    }
}
