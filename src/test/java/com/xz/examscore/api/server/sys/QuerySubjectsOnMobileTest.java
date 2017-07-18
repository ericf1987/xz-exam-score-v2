package com.xz.examscore.api.server.sys;

import com.xz.ajiaedu.common.ajia.Param;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * @author by fengye on 2017/7/18.
 */
public class QuerySubjectsOnMobileTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    QuerySubjectsOnMobile querySubjectsOnMobile;

    @Test
    public void testExecute() throws Exception {

        Param param = new Param().setParameter("projectId", "430200-13e01c025ac24c6497d916551b3ae7a6");

        Result execute = querySubjectsOnMobile.execute(param);

        System.out.println(execute.getData().toString());
    }
}