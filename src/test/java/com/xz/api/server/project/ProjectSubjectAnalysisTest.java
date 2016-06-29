package com.xz.api.server.project;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.api.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2016/6/16.
 */
public class ProjectSubjectAnalysisTest extends XzExamScoreV2ApplicationTests{

    @Autowired
    ProjectSubjectAnalysis projectSubjectAnalysis;

    @Test
    public void testExecute() throws Exception {
        String schoolId = "002e02d6-c036-4780-85d4-e54e3f1fbf9f";

        Param param = new Param()
                .setParameter("projectId", XT_PROJECT_ID)
                .setParameter("subjectId", (String) null)
                .setParameter("schoolIds", schoolId);
        System.out.println(projectSubjectAnalysis.execute(param));
    }
}