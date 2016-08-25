package com.xz.examscore.api.server.school.compare;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.api.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2016/7/25.
 */
public class SchoolExcellentCompareAnalysisTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    SchoolExcellentCompareAnalysis schoolExcellentCompareAnalysis;

    @Test
    public void testExecute() throws Exception {
        Param param = new Param()
                .setParameter("projectId", "430100-e7bd093d92d844819c7eda8b641ab6ee")
                .setParameter("schoolId", "d00faaa0-8a9b-45c4-ae16-ea2688353cd0")
                .setParameter("subjectId", "001");
        System.out.println(schoolExcellentCompareAnalysis.execute(param).getData());
    }

}