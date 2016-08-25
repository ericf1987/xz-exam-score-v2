package com.xz.examscore.api.server.school.compare;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.api.Param;
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
                .setParameter("projectId", "430100-e7bd093d92d844819c7eda8b641ab6ee")
                .setParameter("schoolId", "d00faaa0-8a9b-45c4-ae16-ea2688353cd0")
                .setParameter("subjectId", "001");
        Result result = schoolPassCompareAnalysis.execute(param);
        System.out.println(result.getData());
    }
}