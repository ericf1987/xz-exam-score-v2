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
                .setParameter("projectId", "433100-4cf7b0ef86574a1598481ba3e3841e42")
                .setParameter("schoolId", "64a1c8cd-a9b9-4755-a973-e1ce07f3f70a")
                .setParameter("subjectId", "001");
        Result result = schoolPassCompareAnalysis.execute(param);
        System.out.println(result.getData());
    }
}