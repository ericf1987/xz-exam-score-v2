package com.xz.api.server.project;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.services.SchoolService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * (description)
 * created at 16/06/29
 *
 * @author yiding_he
 */
public class ProjectQuestTypeAnalysisTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    SchoolService schoolService;

    @Autowired
    ProjectQuestTypeAnalysis projectQuestTypeAnalysis;

    @Test
    public void testExecute() throws Exception {
        Result result = projectQuestTypeAnalysis.execute(new Param()
                .setParameter("projectId", UNION_PROJECT_ID)
                .setParameter("subjectId", "001")
                .setParameter("schoolIds", "02784aa8-c523-497e-8536-7cd3c23f1126"));

        System.out.println(result);
    }
}