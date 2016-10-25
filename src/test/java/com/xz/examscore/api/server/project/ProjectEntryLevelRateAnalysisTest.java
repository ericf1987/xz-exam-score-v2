package com.xz.examscore.api.server.project;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.api.Param;
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
        Param param = new Param().setParameter("projectId", "433100-fef19389d6ce4b1f99847ab96d2cfeba");
        Result result = projectEntryLevelRateAnalysis.execute(param);
        System.out.println(result.getData());
    }
}