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
                "d00faaa0-8a9b-45c4-ae16-ea2688353cd0"
        };
        Param param = new Param().setParameter("projectId", "430100-553137a1e78741149104526aaa84393e")
                .setParameter("schoolIds", schoolIds);
        Result result = projectBasicDataAnalysis.execute(param);
        System.out.println(result.getData());
    }
}