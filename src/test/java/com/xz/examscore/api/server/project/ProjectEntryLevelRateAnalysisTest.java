package com.xz.examscore.api.server.project;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.ajiaedu.common.ajia.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/10/25.
 */
public class ProjectEntryLevelRateAnalysisTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    ProjectEntryLevelRateAnalysis projectEntryLevelRateAnalysis;

    @Test
    public void testExecute() throws Exception {
        Param param = new Param().setParameter("projectId", "430100-1944e9f7048b48e2b38e35db75be4980");
        Result result = projectEntryLevelRateAnalysis.execute(param);
        System.out.println(result.getData());
    }
}