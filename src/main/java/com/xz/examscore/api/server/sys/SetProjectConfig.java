package com.xz.examscore.api.server.sys;

import com.alibaba.fastjson.JSONObject;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author by fengye on 2016/9/1.
 */
@Function(description = "提交项目配置参数，并发送至阿里云", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "highScoreRatio", type = Type.String, description = "高分段比例", required = true),
        @Parameter(name = "topStudentRatio", type = Type.String, description = "尖子生比例", required = true),
        @Parameter(name = "displayOptions", type = Type.StringArray, description = "报表等第参数展示", required = true),
        @Parameter(name = "scoreLevels", type = Type.StringArray, description = "分数等级", required = true),
        @Parameter(name = "rankLevel", type = Type.StringArray, description = "排名等级", required = true)

})
@Service
public class SetProjectConfig implements Server {

    public static final String[] SCORE_LEVEL_PARAM = new String[]{
            "excellent", "good", "pass", "fail"
    };

    public static final String[] RANK_LEVEL_PARAM = new String[]{
            "A", "B", "C", "D", "E", "F"
    };

    @Autowired
    ProjectConfigService projectConfigService;

    @Autowired
    InterfaceClient interfaceClient;

    public static final Logger LOG = LoggerFactory.getLogger(SetProjectConfig.class);

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        System.out.println(param.toString());
        ProjectConfig projectConfig = convert2Obj(param);
        String projectConfigJson = convert2JSON(param);
        Param _param = new Param().setParameter("projectId", projectId)
                .setParameter("settings", projectConfigJson);
        ApiResponse apiResponse = interfaceClient.setProjectConfig(_param);
        if (apiResponse.isSuccess()) {
            try {
                projectConfigService.updateRankLevelConfig(projectConfig);
                return Result.success("配置保存成功!");
            } catch (Exception e) {
                return Result.fail("配置保存失败!");
            }
        } else {
            return Result.fail("配置保存失败，网络连接异常!");
        }
    }

    //将参数转化为json
    private String convert2JSON(Param param) {
        JSONObject jo = new JSONObject();
        jo.put("scoreLevels", toScoreLevelsMap(param.getStringValues("scoreLevels")));
        jo.put("highScoreRatio", param.getDouble("highScoreRatio"));
        jo.put("topStudentRatio", param.getDouble("topStudentRatio"));
        JSONObject rankLevel = new JSONObject();
        rankLevel.put("standard", toRankLevelsMap(param.getStringValues("rankLevel")));
        rankLevel.put("displayOptions", Arrays.asList(param.getStringValues("displayOptions")));
        jo.put("rankLevel", rankLevel);
        return jo.toString();
    }

    public Map<String, Double> toRankLevelsMap(String[] rankLevels) {
        Map<String, Double> map = new HashMap<>();
        for (int i = 0; i < RANK_LEVEL_PARAM.length; i++) {
            map.put(RANK_LEVEL_PARAM[i], Double.parseDouble(rankLevels[i]));
        }
        return map;
    }

    public Map<String, Double> toScoreLevelsMap(String[] scoreLevels) {
        Map<String, Double> map = new HashMap<>();
        for (int i = 0; i < SCORE_LEVEL_PARAM.length; i++) {
            System.out.println(scoreLevels[i]);
            map.put(SCORE_LEVEL_PARAM[i], Double.parseDouble(scoreLevels[i]));
        }
        return map;
    }

    //将参数转化为ProjectConfig对象
    private ProjectConfig convert2Obj(Param param) {
        ProjectConfig projectConfig = new ProjectConfig();
        projectConfig.setProjectId(param.getString("projectId"));
        projectConfig.setHighScoreRate(param.getDouble("highScoreRatio"));
        projectConfig.setTopStudentRate(param.getDouble("topStudentRatio"));
        projectConfig.setRankLevelCombines(Arrays.asList(param.getStringValues("displayOptions")));
        projectConfig.setScoreLevels(toScoreLevelsMap(param.getStringValues("scoreLevels")));
        projectConfig.setRankLevels(toRankLevelsMap(param.getStringValues("rankLevel")));
        return projectConfig;
    }
}
