package com.xz.examscore.api.server.school.compare;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.ajiaedu.common.ajia.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2016/7/26.
 */
public class SchoolPassCompareAnalysisTest extends XzExamScoreV2ApplicationTests{

    @Autowired
    SchoolPassCompareAnalysis schoolPassCompareAnalysis;

    @Test
    public void testExecute() throws Exception {
        Param param = new Param()
                .setParameter("projectId", "430100-a05db0d05ad14010a5c782cd31c0283f")
                .setParameter("schoolId", "d00faaa0-8a9b-45c4-ae16-ea2688353cd0")
                .setParameter("subjectId", "003");
        Result result = schoolPassCompareAnalysis.execute(param);
        System.out.println(result.getData());
    }
}