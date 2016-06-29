package com.xz.api.server.project;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * (description)
 * created at 16/06/08
 *
 * @author yiding_he
 */
public class ProjectScoreAnalysisTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    ProjectScoreAnalysis projectScoreAnalysis;

    @Test
    public void testExecute() throws Exception {
        String schoolId = "11b66fc2-8a76-41c2-a1b3-5011523c7e47";

        Param param = new Param()
                .setParameter("projectId", XT_PROJECT_ID)
                .setParameter("subjectId", (String) null)
                .setParameter("schoolIds", schoolId);

        Result result = projectScoreAnalysis.execute(param);

        System.out.println(result);
    }
}