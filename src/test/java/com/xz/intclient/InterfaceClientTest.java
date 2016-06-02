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
        Result result = interfaceClient.request("QuerySchoolById", new Param()
                .setParameter("schoolId", "7e34fa5e-9023-4ad4-b4fa-fe4e3d7d1b52"));

        System.out.println(result.isSuccess());
        System.out.println(result.getData());
    }
}