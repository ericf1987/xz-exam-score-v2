package com.xz.intclient;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * (description)
 * created at 16/06/01
 *
 * @author yiding_he
 */
public class InterfaceClientTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    InterfaceClient interfaceClient;

    @Test
    public void testRequest() throws Exception {
/*        Result result = interfaceClient.request("QueryProjectReportConfig", new Param()
                .setParameter("projectId", "430200-b73f03af1d74484f84f1aa93f583caaa"));*/

        Result result = interfaceClient.request("QueryExamSchoolByProject", new Param()
                .setParameter("projectId", "430200-b73f03af1d74484f84f1aa93f583caaa"));

        System.out.println(result.isSuccess());
        System.out.println(result.getData());
    }

}