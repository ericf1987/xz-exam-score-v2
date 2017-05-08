package com.xz.examscore.api.server.project;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.ajiaedu.common.ajia.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2017/3/20.
 */
public class ProjectPointAnalysisTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    ProjectPointAnalysis projectPointAnalysis;

    @Test
    public void testExecute() throws Exception {
        String projectId = "430200-9583fddde42d42b2b480b1c5c8cdaf82";
        String[] schoolIds = new String[]{"7e34fa5e-9023-4ad4-b4fa-fe4e3d7d1b52"};
        String subjectId = "003";

        Param param = new Param().setParameter("projectId", projectId)
                .setParameter("schoolIds", schoolIds)
                .setParameter("subjectId", subjectId);

        Result result = projectPointAnalysis.execute(param);
        System.out.println(result.getData());
    }
}