package com.xz.intclient;

import com.alibaba.fastjson.JSONObject;
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

        Result result = interfaceClient.request("QueryProjectReportConfig", new Param()
                .setParameter("projectId", "430200-b73f03af1d74484f84f1aa93f583caaa"));

        System.out.println(result.isSuccess());
        System.out.println(result.getData());
    }

    @Test
    public void testQueryProjectReportConfig() throws Exception {
        Result result = interfaceClient.request("QueryProjectReportConfig",
                new Param().setParameter("projectId", "430100-6402d0adc8d241be947c309b13a5292a"));

        JSONObject rankLevel = result.get("rankLevel");
        System.out.println(result.isSuccess());
        if(null == rankLevel){
            System.out.println("123");
        }else{
            System.out.println(rankLevel.toString());
        }
        System.out.println(result.getData());
    }

}