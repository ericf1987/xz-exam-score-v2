package com.xz.examscore.api.server.sys;

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
                .setParameter("scoreLevels", "0.9, 0.8, 0.7, 0")
                .setParameter("rankLevel", "40, 25, 23, 7, 4, 1")
                .setParameter("highScoreRatio", "0.3").setParameter("topStudentRatio", "0.05");
        System.out.println(param.getDouble("highScoreRatio"));
        System.out.println(Arrays.asList(param.getStringValues("rankLevel")).toString());
        System.out.println(setProjectConfig.toScoreLevelsMap(param.getStringValues("scoreLevels")));
        System.out.println(setProjectConfig.toRankLevelsMap(param.getStringValues("rankLevel")));
    }
}