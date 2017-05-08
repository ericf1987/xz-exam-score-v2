package com.xz.examscore.api.server.project;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.ajiaedu.common.ajia.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2016/8/1.
 */
public class ProjectSubjectiveAnalysisTest extends XzExamScoreV2ApplicationTests {
    @Autowired
    ProjectSubjectiveAnalysis projectSubjectiveAnalysis;

    @Test
    public void testExecute() throws Exception {
        String projectId = "430100-2df3f3ad199042c39c5f4b69f5dc7840";
        String[] schoolIds = new String[]{
                "d00faaa0-8a9b-45c4-ae16-ea2688353cd0"
        };
        String subjectId = "003";

        Param param = new Param()
                .setParameter("projectId", projectId)
                .setParameter("schoolIds", schoolIds)
                .setParameter("subjectId", subjectId);

        System.out.println(projectSubjectiveAnalysis.execute(param).getData());

    }
}