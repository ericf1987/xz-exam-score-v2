package com.xz.examscore.api.server.school;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.api.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/10/25.
 */
public class SchoolEntryLevelRateAnalysisTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    SchoolEntryLevelRateAnalysis schoolEntryLevelRateAnalysis;

    @Test
    public void testExecute() throws Exception {
        Param param = new Param().setParameter("projectId", "430100-1944e9f7048b48e2b38e35db75be4980")
                .setParameter("schoolId", "80e503de-072c-4e26-845f-271e841bf47a");
        Result result = schoolEntryLevelRateAnalysis.execute(param);
        System.out.println(result.getData());
    }
}