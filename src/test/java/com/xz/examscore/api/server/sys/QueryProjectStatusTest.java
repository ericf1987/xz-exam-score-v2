package com.xz.examscore.api.server.sys;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.ajiaedu.common.ajia.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2016/8/6.
 */
public class QueryProjectStatusTest extends XzExamScoreV2ApplicationTests {
    @Autowired
    QueryProjectStatus queryProjectStatus;

    @Test
    public void testExecute() throws Exception {
        String projectId = "430100-3ec14ff630824fa48af9bf03b33313bb";
        Param param = new Param().setParameter("projectId", projectId);
        Result result = queryProjectStatus.execute(param);
        System.out.println(result.getData());
    }
}