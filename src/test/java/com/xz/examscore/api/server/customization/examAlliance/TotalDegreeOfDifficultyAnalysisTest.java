package com.xz.examscore.api.server.customization.examAlliance;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.ajiaedu.common.ajia.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/12/26.
 */
public class TotalDegreeOfDifficultyAnalysisTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    TotalDegreeOfDifficultyAnalysis totalDegreeOfDifficultyAnalysis;

    @Test
    public void testExecute() throws Exception {
        Param param = new Param().setParameter("projectId", "430200-3e67c524f149491597279ef6ae31baef");
        Result result = totalDegreeOfDifficultyAnalysis.execute(param);
        System.out.println(result.getData());
    }
}