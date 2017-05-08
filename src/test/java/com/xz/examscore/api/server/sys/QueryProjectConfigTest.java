package com.xz.examscore.api.server.sys;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.ajiaedu.common.ajia.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2016/9/1.
 */
public class QueryProjectConfigTest extends XzExamScoreV2ApplicationTests {
    @Autowired
    QueryProjectConfig queryProjectConfig;
    @Test
    public void testExecute() throws Exception {
        String projectId = "430500-79fbd04d11fe4e12ac43fe484e6fa735";
        Param param = new Param().setParameter("projectId", projectId);
        System.out.println(queryProjectConfig.execute(param).getData().get("projectConfig"));
    }
}