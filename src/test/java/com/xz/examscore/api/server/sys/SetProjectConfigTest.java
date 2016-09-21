package com.xz.examscore.api.server.sys;

import com.alibaba.fastjson.JSONObject;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.api.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/9/20.
 */
public class SetProjectConfigTest extends XzExamScoreV2ApplicationTests {
    @Autowired
    SetProjectConfig setProjectConfig;

    @Test
    public void testExecute() throws Exception {
        Param param = new Param()
                .setParameter("scoreLevels", "0.9")
                .setParameter("rankLevel", "40")
                .setParameter("highScoreRatio", "0.3").setParameter("topStudentRatio", "0.05");
        System.out.println(setProjectConfig.convert2JSON(param));
        System.out.println(param.getDouble("highScoreRatio"));
        System.out.println(Arrays.asList(param.getStringValues("rankLevel")).toString());
        System.out.println(setProjectConfig.toScoreLevelsMap(param.getStringValues("scoreLevels")));
        System.out.println(setProjectConfig.toRankLevelsMap(param.getStringValues("rankLevel")));
    }

    @Test
    public void testExecute1() throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("test1", "123");
        jo.put("test2", "123");
        jo.put("test3", "123");
        JSONObject rankLevel = new JSONObject();
        rankLevel.put("rank1", "rank1");
        rankLevel.put("rank2", "rank2");
        rankLevel.put("rank3", "rank3");
        jo.put("rankLevel", rankLevel);
        System.out.println(jo.toJSONString());
    }
}