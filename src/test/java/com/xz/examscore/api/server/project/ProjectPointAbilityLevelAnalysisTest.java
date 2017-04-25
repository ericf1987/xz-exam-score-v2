package com.xz.examscore.api.server.project;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.api.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2016/7/30.
 */
public class ProjectPointAbilityLevelAnalysisTest extends XzExamScoreV2ApplicationTests {
    @Autowired
    ProjectPointAbilityLevelAnalysis projectPointAbilityLevelAnalysis;

    @Test
    public void testExecute() throws Exception {
        Param param = new Param().setParameter("projectId", "430100-dd3013ab961946fb8a3668e5ccc475b6")
                .setParameter("subjectId", "007")
                .setParameter("schoolId", "d9bdecc9-0185-4688-90d1-1aaf27e2dcfd");
        Result result = projectPointAbilityLevelAnalysis.execute(param);
        System.out.println(result.getData());
    }
}