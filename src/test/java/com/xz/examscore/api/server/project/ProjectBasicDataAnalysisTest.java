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
                "528654bb-3529-4ef2-9d71-5870d3f55d49"
        };
        Param param = new Param().setParameter("projectId", "430200-215a0a158e0244d79cf3c9924dc6691c")
                .setParameter("schoolIds", schoolIds);
        Result result = projectBasicDataAnalysis.execute(param);
        System.out.println(result.getData());
    }
}