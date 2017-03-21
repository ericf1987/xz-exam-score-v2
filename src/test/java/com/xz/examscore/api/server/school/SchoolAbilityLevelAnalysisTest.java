package com.xz.examscore.api.server.school;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.api.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2017/3/20.
 */
public class SchoolAbilityLevelAnalysisTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    SchoolAbilityLevelAnalysis schoolAbilityLevelAnalysis;

    @Test
    public void testExecute() throws Exception {
        String projectId = "430200-9583fddde42d42b2b480b1c5c8cdaf82";
        String schoolId = "7e34fa5e-9023-4ad4-b4fa-fe4e3d7d1b52";
        String subjectId = "001";

        Param param = new Param().setParameter("projectId", projectId)
                .setParameter("schoolId", schoolId)
                .setParameter("subjectId", subjectId);

        Result result = schoolAbilityLevelAnalysis.execute(param);
        System.out.println(
                result.getData()
        );
    }
}