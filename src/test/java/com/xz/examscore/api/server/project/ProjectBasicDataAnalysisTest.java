package com.xz.examscore.api.server.project;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.api.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2016/6/24.
 */
public class ProjectBasicDataAnalysisTest extends XzExamScoreV2ApplicationTests{

    @Autowired
    ProjectBasicDataAnalysis projectBasicDataAnalysis;

    @Test
    public void testExecute() throws Exception {
        String[] schoolIds = new String[]{
                "b49b8e85-f390-4e09-a709-8ab1175b0c68",
                "03cd1f9c-b418-41ca-b3b8-9eb3af60ac3e",
                "091c8d3b-9f84-4cf4-a4fa-7bc8029ff693",
                "15630357-bcb4-4eac-b96c-bfa232b088db",
                "28daa47d-b783-4d5c-9515-7e29c3a976f0",
                "348a762e-f63f-40b7-b7e2-17c650044dc3"
        };
        Param param = new Param().setParameter("projectId", "431100-903288f61a5547f1a08a7e20420c4e9e")
                .setParameter("schoolIds", schoolIds);
        Result result = projectBasicDataAnalysis.execute(param);
        System.out.println(result.getData());
    }
}