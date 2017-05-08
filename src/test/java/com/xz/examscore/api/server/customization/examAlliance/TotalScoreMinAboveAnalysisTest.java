package com.xz.examscore.api.server.customization.examAlliance;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.ajiaedu.common.ajia.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2017/1/11.
 */
public class TotalScoreMinAboveAnalysisTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    TotalScoreMinAboveAnalysis totalScoreMinAboveAnalysis;

    @Test
    public void testExecute() throws Exception {
        Param param = new Param().setParameter("projectId", "430100-501b96776dc348748e2afdb95d491516")
                .setParameter("max", "650")
                .setParameter("min", "350")
                .setParameter("span", "10");
        Result result = totalScoreMinAboveAnalysis.execute(param);
        System.out.println(result.getData());
    }
}