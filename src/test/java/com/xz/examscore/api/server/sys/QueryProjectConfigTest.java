package com.xz.examscore.api.server.sys;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.api.Param;
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
        String projectId = "430100-cb04005aa5ae460fae6b9d87df797066";
        Param param = new Param().setParameter("projectId", projectId);
        System.out.println(queryProjectConfig.execute(param).getData().get("projectConfig"));
    }
}