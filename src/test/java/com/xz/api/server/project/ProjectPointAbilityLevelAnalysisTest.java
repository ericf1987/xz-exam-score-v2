package com.xz.api.server.project;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/7/30.
 */
public class ProjectPointAbilityLevelAnalysisTest extends XzExamScoreV2ApplicationTests {
    @Autowired
    ProjectPointAbilityLevelAnalysis projectPointAbilityLevelAnalysis;

    @Test
    public void testExecute() throws Exception {
        Param param = new Param().setParameter("projectId", "430100-e7bd093d92d844819c7eda8b641ab6ee")
                .setParameter("subjectId", "001")
                .setParameter("schoolId", "d00faaa0-8a9b-45c4-ae16-ea2688353cd0");
        Result result = projectPointAbilityLevelAnalysis.execute(param);
        System.out.println(result.getData());
    }
}