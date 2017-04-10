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
                "58272720-c0de-4906-9309-2bcc4b4b13b6",
                "0af7ea56-2da9-450c-8784-bdf9a9473425",
                "23c4362f-f53a-4c66-afe7-efa1964f4a62",
                "24441546-33fc-4ca9-8976-742019718eb9",
                "721d24ce-f52d-4c3b-86ff-0d8a23c12e88",
                "83f70551-9b40-4c27-b90d-846abbfb49b3",
                "c06e1f54-16ee-4bf1-810b-cdfc0ba3d8ff",
                "f35aca4f-961c-4ef8-82f5-44b1089d7243"
        };
        Param param = new Param().setParameter("projectId", "430500-3860909d0fd14c709f86954c2e6d2f56")
                .setParameter("schoolIds", schoolIds);
        Result result = projectBasicDataAnalysis.execute(param);
        System.out.println(result.getData());
    }
}